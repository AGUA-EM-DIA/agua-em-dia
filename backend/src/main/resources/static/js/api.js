/* =====================================================================
   ÁGUA EM DIA — conexão com a API (backend Spring Boot)
   Todas as chamadas ao servidor passam por aqui. O app.js usa este objeto
   em vez de dados guardados no navegador.
   O token de acesso (JWT) fica em localStorage ("manter conectado") ou
   sessionStorage (só nesta aba) e vai no cabeçalho Authorization.
   ===================================================================== */
const API = (() => {
  const CHAVE_TOKEN = "aed_token";
  let token = lerToken();

  function lerToken(){
    try{ return localStorage.getItem(CHAVE_TOKEN) || sessionStorage.getItem(CHAVE_TOKEN) || null; }catch(e){ return null; }
  }
  function guardarToken(valor, lembrar){
    token = valor;
    try{
      localStorage.removeItem(CHAVE_TOKEN); sessionStorage.removeItem(CHAVE_TOKEN);
      if(valor) (lembrar ? localStorage : sessionStorage).setItem(CHAVE_TOKEN, valor);
    }catch(e){ /* navegador sem armazenamento: o login vale só até recarregar */ }
  }

  /** Erro devolvido pela API. "mensagem" já vem em português, pronta para mostrar ao cidadão. */
  class ErroApi extends Error {
    constructor(status, mensagem, campos){ super(mensagem); this.status = status; this.campos = campos || {}; }
  }

  async function requisitar(metodo, caminho, corpo){
    const cab = {};
    if(token) cab["Authorization"] = "Bearer " + token;
    let opcoes = {method:metodo, headers:cab};
    if(corpo instanceof FormData){ opcoes.body = corpo; }               // multipart: o navegador define o Content-Type
    else if(corpo !== undefined){ cab["Content-Type"] = "application/json"; opcoes.body = JSON.stringify(corpo); }

    let resp;
    try{ resp = await fetch(caminho, opcoes); }
    catch(e){ throw new ErroApi(0, "Sem conexão com o servidor. Confira a internet e tente de novo."); }

    if(resp.status === 204) return null;
    let dados = null;
    try{ dados = await resp.json(); }catch(e){ /* resposta sem corpo */ }
    if(!resp.ok){
      if(resp.status === 401 && token){                                  // sessão vencida ou token inválido
        guardarToken(null);
        window.dispatchEvent(new CustomEvent("aed:sessao-expirada"));
      }
      throw new ErroApi(resp.status, (dados && dados.mensagem) || "Algo deu errado. Tente de novo.", dados && dados.campos);
    }
    return dados;
  }

  /** Monta o corpo multipart: parte "dados" (JSON) e, se houver, a parte "foto". */
  function multipart(dados, foto){
    const f = new FormData();
    f.append("dados", new Blob([JSON.stringify(dados)], {type:"application/json"}));
    if(foto) f.append("foto", foto, "foto.jpg");
    return f;
  }

  return {
    ErroApi,
    temToken: () => !!token,

    /* ----- configuração e acesso ----- */
    config:        () => requisitar("GET",  "/api/config"),
    cadastrar:     d  => requisitar("POST", "/api/usuarios", d),
    async entrar(email, senha, lembrar){
      const r = await requisitar("POST", "/api/auth/login", {email, senha});
      guardarToken(r.token, lembrar);
      return r.usuario;
    },
    sair:          () => guardarToken(null),
    eu:            () => requisitar("GET",  "/api/usuarios/me"),
    estatisticas:  () => requisitar("GET",  "/api/usuarios/me/estatisticas"),

    /* ----- recuperação de senha ----- */
    pedirCodigo:   email => requisitar("POST", "/api/auth/recuperacao", {email}),
    redefinirSenha:(email, codigo, novaSenha) => requisitar("POST", "/api/auth/redefinir-senha", {email, codigo, novaSenha}),

    /* ----- login com Google (só funciona com GOOGLE_CLIENT_ID configurado no servidor) ----- */
    async google(credential, lembrar){
      const r = await requisitar("POST", "/api/auth/google", {credential});
      if(r.login) guardarToken(r.login.token, lembrar);
      return r;
    },
    async googleCompletar(dados, lembrar){
      const r = await requisitar("POST", "/api/auth/google/completar", dados);
      guardarToken(r.token, lembrar);
      return r.usuario;
    },

    /* ----- vazamentos ----- */
    listarVazamentos: () => requisitar("GET",  "/api/vazamentos"),
    registrar:       (dados, foto) => requisitar("POST", "/api/vazamentos", multipart(dados, foto)),
    editarDescricao: (id, descricao) => requisitar("PATCH", "/api/vazamentos/" + id + "/descricao", {descricao}),
    apoiar:          id => requisitar("POST",  "/api/vazamentos/" + id + "/apoios"),
    avaliar:         (id, nota, comentario) => requisitar("POST", "/api/vazamentos/" + id + "/avaliacao", {nota, comentario}),
    marcarVistos:    () => requisitar("POST",  "/api/vazamentos/meus/vistos"),
    /* Só funciona no modo demonstração (painel de operação) ou com a chave de serviço no servidor */
    atualizarSituacao: (id, situacao) => requisitar("PATCH", "/api/vazamentos/" + id + "/situacao", {situacao}),

    /* ----- identificação por foto (Gemini) e assistente ----- */
    identificarFoto: foto => { const f = new FormData(); f.append("foto", foto, "foto.jpg"); return requisitar("POST", "/api/ia/identificar", f); },
    perguntarAquari: texto => requisitar("POST", "/api/aquari/mensagens", {texto})
  };
})();

/* Converte um vazamento devolvido pela API para o formato que as telas do app.js já usam.
   (completarLocal e USUARIO vêm do app.js.) */
function chamadoDaApi(v){
  const local = completarLocal({lat:Number(v.latitude), lng:Number(v.longitude)});
  return {
    id:v.id, protocolo:v.protocolo, simulado:false, meu:v.meu,
    tipo:v.tipo, porte:v.porte,
    lat:local.lat, lng:local.lng, x:local.x, y:local.y, bairro:local.bairro,
    referencia:v.endereco || ("Perto de " + local.bairro),
    autor:v.autor, vazao:v.vazaoLitrosHora, metaH:v.metaHoras,
    abertoEm:v.criadoEm, situacao:v.situacao, horasAteReparo:v.horasAteReparo,
    avaliacao:v.avaliacao ? {nota:v.avaliacao.nota, comentario:v.avaliacao.comentario || "", quando:v.avaliacao.criadoEm} : null,
    apoiosTotal:v.apoios, apoiadoPorMim:v.apoiadoPorMim,
    foto:v.fotoUrl || null, descricao:v.descricao || "",
    despachadoEm:v.despachadoEm, concluidoEm:v.concluidoEm,
    ultimaSituacaoVista: v.novidadeNaoVista ? "pendente" : v.situacao
  };
}
