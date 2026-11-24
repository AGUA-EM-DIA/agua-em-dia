/* =====================================================================
   ÁGUA EM DIA — protótipo navegável
   Espelha as classes do sistema Java: Ocorrencia (abstrata) ->
   VazamentoAdutora, VazamentoRede, VazamentoRamal, LigacaoClandestina,
   DesperdicioVisivel; Reparo; Bairro; Cidadao.
   ===================================================================== */

/* Chave do Google Maps: vem do servidor (GOOGLE_MAPS_API_KEY), nunca fica escrita no código.
   Restrinja a chave por referenciador HTTP e defina cota diária no Google Cloud Console.
   Sem chave, o app usa o mapa esquemático. Para testar uma chave só no seu navegador:
   localStorage.setItem("aed_chave_maps","SUA_CHAVE") e recarregue. */
/* Configuração pública do servidor (modo demonstração, IA, Google). Preenchida em iniciar(). */
let CONFIG = {demo:false, iaDisponivel:false, googleClientId:"", mapsKey:""};
let CHAVE_MAPS = (() => { try{ return localStorage.getItem("aed_chave_maps") || ""; }catch(e){ return ""; } })();
const CENTRO_RECIFE = {lat:-8.0578, lng:-34.8829};

/* ---------- ícones ---------- */
const ICO = {
  zap:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 20l1.3-4A8 8 0 1 1 8.2 18.8L4 20Z"/><path d="M9 9.5c.4 2 2.5 4.2 4.8 4.8l1.2-1.2-1.7-1-.8.7c-.9-.4-1.7-1.2-2.1-2.1l.7-.8-1-1.7L9 9.5Z"/></svg>',
  gota:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3s-6 7-6 10a6 6 0 0 0 12 0c0-3-6-10-6-10Z"/></svg>',
  cano:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 8h8v8H3z"/><path d="M11 12h10"/><path d="M17 9v6"/></svg>',
  rua:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 21 8 3M20 21 16 3M12 5v3M12 11v3M12 17v3"/></svg>',
  casa:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 11 12 4l8 7v9H4z"/><path d="M10 20v-6h4v6"/></svg>',
  proibido:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M6 6l12 12"/></svg>',
  torneira:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M6 9h8a4 4 0 0 1 4 4v1"/><path d="M6 6v6"/><path d="M18 18v3"/></svg>',
  mapa:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M9 3 3 6v15l6-3 6 3 6-3V3l-6 3-6-3Z"/><path d="M9 3v15M15 6v15"/></svg>',
  mais:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 5v14M5 12h14"/></svg>',
  lista:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M8 6h12M8 12h12M8 18h12M4 6h.01M4 12h.01M4 18h.01"/></svg>',
  check:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M20 6 9 17l-5-5"/></svg>',
  caminhao:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 7h11v8H3z"/><path d="M14 10h4l3 3v2h-7z"/><circle cx="7" cy="18" r="2"/><circle cx="17" cy="18" r="2"/></svg>',
  alfinete:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 21s7-6.2 7-11a7 7 0 1 0-14 0c0 4.8 7 11 7 11Z"/><circle cx="12" cy="10" r="2.5"/></svg>',
  alvo:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="7"/><circle cx="12" cy="12" r="1.6" fill="currentColor" stroke="none"/><path d="M12 2v3M12 19v3M2 12h3M19 12h3"/></svg>',
  aviso:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M12 3 2 20h20L12 3Z"/><path d="M12 10v4M12 17h.01"/></svg>',
  lupa:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/></svg>',
  carta:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="2"/><path d="m3 7 9 6 9-6"/></svg>',
  bairro:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M3 21h18M5 21V8l5-4 5 4v13M9 21v-5h3v5"/><path d="M17 21v-8h4v8"/></svg>',
  compartilhar:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><circle cx="18" cy="5" r="2.6"/><circle cx="6" cy="12" r="2.6"/><circle cx="18" cy="19" r="2.6"/><path d="M8.3 10.7 15.7 6.3M8.3 13.3l7.4 4.4"/></svg>',
  info:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="12" r="9"/><path d="M12 11v5.5M12 8v.01"/></svg>',
  fechar:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M6 6l12 12M18 6 6 18"/></svg>',
  grafico:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 20V10M12 20V4M20 20v-7"/></svg>',
  perfil:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><circle cx="12" cy="8" r="4"/><path d="M4 20c0-4.4 3.6-7 8-7s8 2.6 8 7"/></svg>',
  telefone:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M6.5 3h3l1.5 5-2 1.5a11 11 0 0 0 5.5 5.5l1.5-2 5 1.5v3a2 2 0 0 1-2.2 2A17 17 0 0 1 4.5 5.2 2 2 0 0 1 6.5 3Z"/></svg>',
  camera:'<svg class="ico" viewBox="0 0 24 24" aria-hidden="true"><path d="M4 8h3l2-3h6l2 3h3v11H4z"/><circle cx="12" cy="13.5" r="3.5"/></svg>'
};

/* ---------- tabelas de referência ---------- */
const PORTES = [
  {id:"GOTEJAMENTO", nome:"Gotejamento contínuo", desc:"Pinga sem parar, molha o pé do poste", lh:3,    barras:[5,0,0,0,0]},
  {id:"FIO",         nome:"Fio d'água",           desc:"Escorre fino e constante na calçada",  lh:60,   barras:[5,9,0,0,0]},
  {id:"MEDIO",       nome:"Jato médio",           desc:"Empoça a rua, dá para ouvir de perto",  lh:600,  barras:[5,9,13,0,0]},
  {id:"FORTE",       nome:"Jato forte",           desc:"Espirra alto, corre pela sarjeta",      lh:2400, barras:[5,9,13,16,0]},
  {id:"JORRO",       nome:"Jorro / gêiser",       desc:"Sobe acima do meio-fio, alaga a via",   lh:9000, barras:[5,9,13,16,20]}
];

const TIPOS = [
  {id:"ADUTORA",     nome:"Vazamento em adutora",    desc:"Cano grosso, geralmente na via principal",  mult:3.0, metaH:4,   ico:"cano"},
  {id:"REDE",        nome:"Vazamento na rede da rua", desc:"Asfalto afundado, água brotando do chão",   mult:1.6, metaH:24,  ico:"rua"},
  {id:"RAMAL",       nome:"Vazamento no cavalete",    desc:"Registro, hidrômetro ou ligação da casa",   mult:0.7, metaH:72,  ico:"casa"},
  {id:"CLANDESTINA", nome:"Ligação clandestina",      desc:"Mangueira ou cano puxando água sem medidor", mult:0,  metaH:168, ico:"proibido", fixa:800},
  {id:"DESPERDICIO", nome:"Desperdício visível",      desc:"Hidrante aberto, caixa transbordando, calçada lavada", mult:0.5, metaH:48, ico:"torneira"}
];

/* Bairros com coordenada real (Google) e posição no mapa esquemático (fallback) */
const BAIRROS = [
  {nome:"Recife Antigo", lat:-8.0620, lng:-34.8715, x:497, y:296},
  {nome:"Santo Amaro",   lat:-8.0430, lng:-34.8860, x:392, y:228},
  {nome:"Boa Vista",     lat:-8.0570, lng:-34.8880, x:378, y:322},
  {nome:"Madalena",      lat:-8.0530, lng:-34.9110, x:276, y:362},
  {nome:"Torre",         lat:-8.0470, lng:-34.9080, x:236, y:330},
  {nome:"Casa Amarela",  lat:-8.0244, lng:-34.9147, x:238, y:176},
  {nome:"Água Fria",     lat:-8.0130, lng:-34.8930, x:330, y:120},
  {nome:"Beberibe",      lat:-8.0010, lng:-34.8830, x:392, y:88},
  {nome:"Várzea",        lat:-8.0500, lng:-34.9560, x:104, y:378},
  {nome:"Afogados",      lat:-8.0745, lng:-34.9110, x:298, y:452},
  {nome:"Imbiribeira",   lat:-8.1010, lng:-34.9080, x:386, y:502},
  {nome:"Pina",          lat:-8.0930, lng:-34.8830, x:466, y:396},
  {nome:"Boa Viagem",    lat:-8.1240, lng:-34.9010, x:448, y:568},
  {nome:"Ibura",         lat:-8.1210, lng:-34.9430, x:326, y:652},
  {nome:"Jordão",        lat:-8.1330, lng:-34.9280, x:404, y:664},
  {nome:"Cohab",         lat:-8.1360, lng:-34.9490, x:296, y:722}
];

const LITROS_FAMILIA_MES = 12000;
const CORES = {alta:"#FF5A36", media:"#F5B02A", baixa:"#6CADDF", resolvido:"#38C172"};

/* ---------- estado ---------- */

const estado = {
  zap:{msgs:[], naoLidas:0}, gmail:{msgs:[]}, gmailVista:"lista", gmailAberto:null, recup:null, app:"home", zapVista:"lista",
  vista:"app",
  telaApp:"mapa",        // mapa · registrar · chamados
  passo:1,
  rascunho:null,
  ultimo:null,
  chamados:[],
  proximoId:148,
  selecionado:null,
  recemChegado:null,
  avaliacaoRascunho:null,   // {protocolo, nota, comentario} — rascunho da avaliação em andamento
  rankingModoOp:"bairro",   // "bairro" ou "tipo" — alterna o agrupamento do ranking no painel
  detalheAberto:null,       // protocolo do chamado com o modal de detalhe expandido aberto
  litrosNaTela:0,
  busca:{texto:"", sugestoes:[], carregando:false},
  filtroFila:{bairro:"todos", tipo:"todos", situacao:"todos", texto:""},
  local:{status:"inativo", lat:null, lng:null, precisao:null, mensagem:"", jaPerguntou:false},
  google:{pronto:false, falhou:false, mapaApp:null, mapaOp:null, pinos:{}, pinoRascunho:null}
};

/* =====================================================================
   FOTO ILUSTRATIVA DO CHAMADO
   Não é uma foto real (o protótipo não tem câmera de verdade nem
   fotos de ruas reais). É um ícone gerado por tipo de ocorrência, com
   respingos posicionados de forma pseudo-aleatória a partir do próprio
   protocolo — assim cada chamado tem uma "carinha" própria e consistente
   toda vez que a tela é redesenhada, sem depender de imagem externa.
   ===================================================================== */
const GRADIENTE_POR_TIPO = {
  ADUTORA:     ["#3E8FC9", "#123A57"],
  REDE:        ["#6CADDF", "#16304F"],
  RAMAL:       ["#5CC9DB", "#1B4653"],
  CLANDESTINA: ["#FF7A5C", "#7A2214"],
  DESPERDICIO: ["#F5C15A", "#7A4E0A"]
};

/** Hash simples e estável: mesmo texto sempre gera o mesmo número. */
function hashTexto(t){
  let h = 0;
  for(let i = 0; i < t.length; i++){
    h = (h * 31 + t.charCodeAt(i)) >>> 0;
  }
  return h;
}

/** Pseudo-aleatório determinístico a partir de uma semente numérica. */
function aleatorioSeed(seed){
  const x = Math.sin(seed) * 10000;
  return x - Math.floor(x);
}

/** SVG com respingos + ícone do tipo, usado como miniatura do chamado. */
function fotoChamadoSVG(chamadoOuTipo){
  const tipoId = typeof chamadoOuTipo === "string" ? chamadoOuTipo : chamadoOuTipo.tipo;
  const semente = hashTexto(typeof chamadoOuTipo === "string" ? chamadoOuTipo
                                                                : chamadoOuTipo.protocolo);
  const t = TIPOS.find(x => x.id === tipoId);
  let gotas = "";
  for(let i = 0; i < 5; i++){
    const r1 = aleatorioSeed(semente + i * 7.13);
    const r2 = aleatorioSeed(semente + i * 3.71 + 1);
    const cx = (8 + r1 * 48).toFixed(1);
    const cy = (8 + r2 * 48).toFixed(1);
    const raio = (2 + r1 * 3).toFixed(1);
    gotas += `<circle cx="${cx}" cy="${cy}" r="${raio}" fill="#fff" opacity="${(0.10 + r2 * 0.16).toFixed(2)}"/>`;
  }
  const iconeMiolo = ICO[t.ico].replace(/^<svg[^>]*>/, "").replace("</svg>", "");
  return `<svg viewBox="0 0 64 64" width="64" height="64" aria-hidden="true">${gotas}
    <g transform="translate(20 20)">${iconeMiolo}</g></svg>`;
}

/** Div pronta com o gradiente de fundo + a miniatura, para colar no card. */
function blocoFotoChamado(chamadoOuTipo, classeExtra){
  const tipoId = typeof chamadoOuTipo === "string" ? chamadoOuTipo : chamadoOuTipo.tipo;
  const [corA, corB] = GRADIENTE_POR_TIPO[tipoId] || GRADIENTE_POR_TIPO.REDE;
  if(typeof chamadoOuTipo === "object" && chamadoOuTipo.foto){
    return `<div class="foto-chamado tem-foto${classeExtra ? " " + classeExtra : ""}">
      <img src="${chamadoOuTipo.foto}" alt="Foto enviada junto com o chamado"></div>`;
  }
  return `<div class="foto-chamado${classeExtra ? " " + classeExtra : ""}"
             style="background:linear-gradient(135deg, ${corA}, ${corB})">
      ${fotoChamadoSVG(chamadoOuTipo)}
    </div>`;
}

/* =====================================================================
   CIDADÃOS COLABORADORES
   Agrega os chamados por autor. É a mesma ideia da classe Cidadao no
   sistema Java: reconhecer quem mais ajuda a mapear a rede.
   ===================================================================== */
const CORES_AVATAR = ["#6CADDF", "#F5B02A", "#38C172", "#FF5A36", "#8B7FE8", "#74B4E8"];

function calcularColaboradores(){
  const mapa = {};
  for(const c of estado.chamados){
    if(!mapa[c.autor]){
      mapa[c.autor] = {nome: c.autor, registros: 0, resolvidos: 0, volume: 0};
    }
    mapa[c.autor].registros++;
    if(c.situacao === "RESOLVIDO") mapa[c.autor].resolvidos++;
    mapa[c.autor].volume += volumePerdido(c);
  }
  return Object.values(mapa).sort((a, b) => b.registros - a.registros || b.volume - a.volume);
}

function iniciaisNome(nome){
  const partes = nome.trim().split(/\s+/);
  const primeira = partes[0] ? partes[0][0] : "";
  const ultima = partes.length > 1 ? partes[partes.length - 1][0] : "";
  return (primeira + ultima).toUpperCase();
}

function pintarColaboradores(){
  const alvo = document.getElementById("colaboradoresOp");
  if(!alvo) return;
  const top = calcularColaboradores().slice(0, 5);
  if(!top.length){
    alvo.innerHTML = `<p style="color:var(--cinza);font-size:13px;margin:0;text-align:center">
      Nenhum registro ainda.</p>`;
    return;
  }
  alvo.innerHTML = top.map((p, i) => {
    const cor = CORES_AVATAR[[...p.nome].reduce((h,ch) => (h*31 + ch.charCodeAt(0)) >>> 0, 0) % CORES_AVATAR.length];
    return `<div class="colaborador${i === 0 ? " primeiro" : ""}">
      ${i === 0 ? '<span class="coroa">👑</span>' : ""}
      <div class="colaborador-avatar" style="background:${cor}">${miolioAvatar(p.nome, iniciaisNome(p.nome))}</div>
      <div class="colaborador-nome">${p.nome}</div>
      <div class="colaborador-meta">${p.registros} registro${p.registros !== 1 ? "s" : ""}
        · ${p.resolvidos} resolvido${p.resolvidos !== 1 ? "s" : ""}</div>
      <div class="colaborador-volume">${fmtLitros(p.volume)} mapeados</div>
    </div>`;
  }).join("");
}

/* Lista as avaliações mais recentes de qualquer chamado — é aqui que a
   operação enxerga o que os cidadãos disseram sobre os reparos, algo que
   antes só aparecia do lado do cidadão em "Meus chamados". */
function pintarAvaliacoesOp(){
  const alvo = document.getElementById("avaliacoesOp");
  if(!alvo) return;
  const avaliados = estado.chamados
    .filter(c => c.avaliacao)
    .sort((a,b) => b.avaliacao.quando - a.avaliacao.quando)
    .slice(0, 6);

  if(!avaliados.length){
    alvo.innerHTML = `<p style="color:var(--cinza);font-size:13px;margin:0;text-align:center">
      Nenhuma avaliação registrada ainda.</p>`;
    return;
  }

  alvo.innerHTML = avaliados.map(c => {
    const t = TIPOS.find(t => t.id === c.tipo);
    const horasDesde = (Date.now() - c.avaliacao.quando) / 3600000;
    const cheias = "★".repeat(c.avaliacao.nota) + "☆".repeat(5 - c.avaliacao.nota);
    return `<div class="item-avaliacao${c.avaliacao.nota <= 2 ? " nota-baixa" : ""}">
      <div class="cabeca-aval">
        <span class="estrelas-fixas" aria-hidden="true">${cheias}</span>
        <span class="quando-aval">há ${fmtHoras(horasDesde)}</span>
      </div>
      <div class="onde-aval">${c.avaliacao.por ? `<span class="aval-por"><span class="avatar mini-av">${miolioAvatar(c.avaliacao.por, c.avaliacao.por.split(" ").map(x => x[0]).join("").slice(0,2).toUpperCase())}</span>${escaparHtml(c.avaliacao.por)}</span> · ` : c.autor + " · "}${t.nome} em ${c.bairro}</div>
      ${c.avaliacao.comentario ? `<p class="relato">${escaparHtml(c.avaliacao.comentario)}”</p>` : ""}
      ${blocoContatoBaixaAvaliacao(c)}
    </div>`;
  }).join("");
}

/* =====================================================================
   FILTROS DA FILA DE ATENDIMENTO
   ===================================================================== */
function popularFiltroBairro(){
  const sel = document.getElementById("filtroBairro");
  if(!sel) return;
  for(const b of BAIRROS){
    const opcao = document.createElement("option");
    opcao.value = b.nome;
    opcao.textContent = b.nome;
    sel.appendChild(opcao);
  }
}
function popularFiltroTipo(){
  const sel = document.getElementById("filtroTipo");
  if(!sel) return;
  for(const t of TIPOS){
    const opcao = document.createElement("option");
    opcao.value = t.id;
    opcao.textContent = t.nome;
    sel.appendChild(opcao);
  }
}

function passaFiltroFila(c){
  const f = estado.filtroFila;
  if(f.bairro !== "todos" && c.bairro !== f.bairro) return false;
  if(f.tipo !== "todos" && c.tipo !== f.tipo) return false;
  if(f.situacao !== "todos" && c.situacao !== f.situacao) return false;
  if(f.texto){
    const alvo = f.texto.toLowerCase();
    if(!c.referencia.toLowerCase().includes(alvo)
       && !c.protocolo.toLowerCase().includes(alvo)
       && !c.bairro.toLowerCase().includes(alvo)){
      return false;
    }
  }
  return true;
}

/** Chamado pelos controles de filtro. Redesenha só a fila, sem
    reconstruir os próprios controles — o foco do campo de texto não
    se perde enquanto a pessoa digita. */
function atualizarFiltroFila(campo, valor){
  estado.filtroFila[campo] = valor;
  pintarFila();
}

function limparFiltrosFila(){
  estado.filtroFila = {bairro:"todos", tipo:"todos", situacao:"todos", texto:""};
  const b = document.getElementById("filtroBairro");
  const t = document.getElementById("filtroTipo");
  const s = document.getElementById("filtroSituacao");
  const x = document.getElementById("filtroTexto");
  if(b) b.value = "todos";
  if(t) t.value = "todos";
  if(s) s.value = "todos";
  if(x) x.value = "";
  pintarFila();
}

function filtrosAtivos(){
  const f = estado.filtroFila;
  return f.bairro !== "todos" || f.tipo !== "todos" || f.situacao !== "todos" || f.texto !== "";
}

/* =====================================================================
   EVOLUÇÃO DA PERDA (7 DIAS)
   Os 6 primeiros dias são uma série ilustrativa em declínio — o histórico
   que levou a rede até o patamar de hoje. A barra "Hoje" é sempre real:
   é a vazão atual (L/h) somada de todos os chamados abertos agora. Se
   você despachar e concluir reparos durante a demonstração, essa barra
   cai ao vivo — é o efeito mais convincente do gráfico.
   ===================================================================== */
const HISTORICO_EVOLUCAO = [
  {rotulo:"-6d", litrosHora: 58000},
  {rotulo:"-5d", litrosHora: 54000},
  {rotulo:"-4d", litrosHora: 51000},
  {rotulo:"-3d", litrosHora: 47000},
  {rotulo:"-2d", litrosHora: 44000},
  {rotulo:"-1d", litrosHora: 41000}
];

function calcularSerieEvolucao(){
  const hoje = estado.chamados
    .filter(c => c.situacao !== "RESOLVIDO")
    .reduce((s,c) => s + c.vazao, 0);
  return [...HISTORICO_EVOLUCAO.map(d => ({...d, hoje:false})),
          {rotulo:"Hoje", litrosHora: hoje, hoje:true}];
}

function pintarGraficoEvolucao(){
  const alvo = document.getElementById("graficoEvolucao");
  if(!alvo) return;
  const serie = calcularSerieEvolucao();
  const maxValor = Math.max(...serie.map(d => d.litrosHora), 1);

  alvo.innerHTML = `<div class="grafico-evolucao" role="img"
      aria-label="Gráfico de vazão diária dos últimos 7 dias: ${
        serie.map(d => d.rotulo + " " + fmt(d.litrosHora) + " litros por hora").join(", ")
      }">` +
    serie.map(d => `<div class="barra-dia${d.hoje ? " hoje" : ""}">
        <span class="valor">${fmt(d.litrosHora)}</span>
        <span class="coluna" style="height:${Math.max(3, d.litrosHora / maxValor * 140)}px"></span>
        <span class="rotulo-dia">${d.rotulo}</span>
      </div>`).join("") +
    `</div>`;

  const primeiro = HISTORICO_EVOLUCAO[0].litrosHora;
  const hojeValor = serie[serie.length - 1].litrosHora;
  const variacao = ((primeiro - hojeValor) / primeiro) * 100;
  const badge = document.getElementById("badgeEvolucao");
  if(badge){
    badge.innerHTML = variacao >= 0
      ? `<span class="selo-reducao queda">▼ ${variacao.toFixed(0)}% em 7 dias</span>`
      : `<span class="selo-reducao alta">▲ ${Math.abs(variacao).toFixed(0)}% em 7 dias</span>`;
  }
}

/* ---------- criação de chamados ---------- */
function criarChamado(d){
  const t = TIPOS.find(t => t.id === d.tipo);
  const p = PORTES.find(p => p.id === d.porte) || PORTES[2];
  const vazao = t.fixa ? t.fixa : Math.round(p.lh * t.mult);
  const local = completarLocal(d);
  return {
    protocolo:"SIM-2026-" + String(estado.proximoId++).padStart(4,"0"),   // chamados simulados nunca são enviados ao servidor
    simulado:true, meu:false, apoiosTotal:(d.apoios || []).length, apoiadoPorMim:false,
    tipo:d.tipo, porte:d.porte,
    lat:local.lat, lng:local.lng, x:local.x, y:local.y, bairro:local.bairro,
    referencia: d.referencia || (ruaFicticia() + " — " + local.bairro),
    autor: d.autor || "Alguém",
    vazao, metaH:t.metaH,
    abertoEm: Date.now() - (d.horasAberto || 0) * 3600000,
    situacao: d.situacao || "ABERTO",
    horasAteReparo: d.horasReparo || null,
    avaliacao: d.avaliacao || null,   // {nota: 1-5, quando} — só existe depois do cidadão avaliar o reparo
    foto: d.foto || null,                    // data URL (JPEG reduzido) da foto anexada, opcional
    descricao: (d.descricao || "").trim(),   // relato livre de quem registrou, opcional
    despachadoEm: d.despachadoEm || null,    // timestamp — usado na linha do tempo
    concluidoEm: d.concluidoEm || null,      // timestamp — usado na linha do tempo
    ultimaSituacaoVista: d.situacao || "ABERTO"   // controla o badge de notificação da Ye
  };
}

/* Aceita coordenada do Google (lat/lng) ou do mapa esquemático (x/y)
   e completa o que faltar usando o bairro mais próximo. */
function completarLocal(d){
  if(d.lat != null && d.lng != null){
    const b = bairroPorCoordenada(d.lat, d.lng);
    return {lat:d.lat, lng:d.lng, bairro:b.nome,
            x: d.x != null ? d.x : b.x + (Math.random()*20-10),
            y: d.y != null ? d.y : b.y + (Math.random()*20-10)};
  }
  const b = bairroPorPosicao(d.x, d.y);
  return {x:d.x, y:d.y, bairro:b.nome,
          lat: b.lat + (Math.random()*0.008-0.004),
          lng: b.lng + (Math.random()*0.008-0.004)};
}

function bairroPorCoordenada(lat,lng){
  let melhor = BAIRROS[0], dist = Infinity;
  for(const b of BAIRROS){
    const d = (b.lat-lat)**2 + (b.lng-lng)**2;
    if(d < dist){ dist = d; melhor = b; }
  }
  return melhor;
}
function bairroPorPosicao(x,y){
  let melhor = BAIRROS[0], dist = Infinity;
  for(const b of BAIRROS){
    const d = (b.x-x)**2 + (b.y-y)**2;
    if(d < dist){ dist = d; melhor = b; }
  }
  return melhor;
}

const RUAS = ["Rua do Sol","Av. Beira Rio","Rua da Aurora","Estrada do Arraial","Av. Caxangá",
  "Rua Real da Torre","Av. Recife","Rua da Harmonia","Av. Mascarenhas de Morais","Rua Imperial",
  "Estrada dos Remédios","Av. Norte"];
function ruaFicticia(){
  return RUAS[Math.floor(Math.random()*RUAS.length)] + ", " + (100 + Math.floor(Math.random()*900));
}

/* ---------- cálculos (métodos da classe Ocorrencia) ---------- */
function horasEmAberto(c){
  return c.situacao === "RESOLVIDO" ? c.horasAteReparo : (Date.now() - c.abertoEm)/3600000;
}
function volumePerdido(c){ return c.vazao * horasEmAberto(c); }
function atraso(c){ return Math.max(0, horasEmAberto(c) - c.metaH) / c.metaH; }
/* Cada pessoa que confirma ("eu também vi") reforça o chamado: +10% de prioridade, até +100%. */
function reforco(c){ return 1 + 0.1 * Math.min(10, c.apoiosTotal || 0); }
function prioridade(c){ return c.vazao * (1 + atraso(c)) * reforco(c); }
function nivel(c){
  if(c.situacao === "RESOLVIDO") return "resolvido";
  const p = prioridade(c);
  return p >= 3000 ? "alta" : p >= 500 ? "media" : "baixa";
}

/* ---------- formatação ---------- */
const fmt = n => Math.round(n).toLocaleString("pt-BR");
function fmtLitros(l){
  if(l >= 1000000) return (l/1000000).toFixed(1).replace(".",",") + " mi L";
  if(l >= 10000)   return fmt(l/1000) + " mil L";
  return fmt(l) + " L";
}
function fmtHoras(h){
  if(h < 1)  return Math.round(h*60) + " min";
  if(h < 48) return Math.round(h) + " h";
  return Math.round(h/24) + " dias";
}
const familias = l => l / LITROS_FAMILIA_MES;

/* ---------- massa de teste ---------- */
function semear(){
  const base = [
    {tipo:"ADUTORA", porte:"JORRO", lat:-8.0752, lng:-34.9098, horasAberto:6,
     referencia:"Av. Recife, altura do viaduto — Afogados", autor:"Renan"},
    {tipo:"REDE", porte:"FORTE", lat:-8.0250, lng:-34.9160, horasAberto:52,
     referencia:"Estrada do Arraial, 1420 — Casa Amarela", autor:"Renan"},
    {tipo:"REDE", porte:"MEDIO", lat:-8.1225, lng:-34.9445, horasAberto:31,
     referencia:"Rua Marcos Freire, 88 — Ibura", autor:"Thales"},
    {tipo:"RAMAL", porte:"FIO", lat:-8.1305, lng:-34.9035, horasAberto:96,
     referencia:"Rua Setúbal, 245 — Boa Viagem", autor:"Thales"},
    {tipo:"CLANDESTINA", porte:"MEDIO", lat:-8.1372, lng:-34.9505, horasAberto:120,
     referencia:"Travessa da Paz — Cohab", autor:"Pedro"},
    {tipo:"DESPERDICIO", porte:"FORTE", lat:-8.0592, lng:-34.8845, horasAberto:3,
     referencia:"Hidrante aberto na Rua da Aurora — Boa Vista", autor:"Carlos"},
    {tipo:"RAMAL", porte:"GOTEJAMENTO", lat:-8.0512, lng:-34.9578, horasAberto:70,
     referencia:"Rua Padre Lemos, 512 — Várzea", autor:"Marina"},
    {tipo:"REDE", porte:"FORTE", lat:-8.0442, lng:-34.8872, horasAberto:14,
     referencia:"Av. Norte, 3100 — Santo Amaro", autor:"Renan"},
    {tipo:"RAMAL", porte:"MEDIO", lat:-8.0538, lng:-34.9125, horasAberto:44, situacao:"REPARO",
     referencia:"Rua Real da Torre, 640 — Madalena", autor:"Thales"},
    {tipo:"REDE", porte:"JORRO", lat:-8.0938, lng:-34.8848, horasAberto:60, situacao:"RESOLVIDO",
     horasReparo:9, referencia:"Av. Antônio de Góes — Pina", autor:"Juliana",
     avaliacao:{nota:5, quando:Date.now() - 8*3600000, contatoRealizado:false}}
  ];
  estado.proximoId = 101;
  base[0].apoios = ["Thales", "Pedro"];
  base[1].apoios = ["Renan"];
  estado.chamados = base.map(criarChamado);
  estado.proximoId = 148;
}

/* =====================================================================
   SIMULAÇÃO DE NOVOS CHAMADOS
   Só para apresentação: a cada intervalo fixo entra um chamado fictício
   novo, como se um morador tivesse acabado de registrar. Mantém a tela
   viva sem exigir que alguém fique cadastrando manualmente durante a
   demonstração. Não faz parte da lógica do sistema (não existe no Java).
   ===================================================================== */
const INTERVALO_NOVO_CHAMADO_MS = 60000;   // 1 minuto — ajuste aqui se quiser mais rápido
const MAXIMO_CHAMADOS_SIMULADOS = 40;      // trava de segurança para apresentações longas

const NOMES_FICTICIOS = ["Renan","Thales","Pedro","Marina","Carlos","Juliana"];
// O nome do usuário logado fica de fora de propósito.
// Se entrasse no sorteio, um chamado aleatório poderia aparecer como
// se fosse da própria pessoa, sem ela ter registrado nada.

/* Distribuição com pesos: vazamento de rede e cavalete são o dia a dia;
   adutora e clandestina aparecem com menos frequência, como na vida real. */
const TIPOS_PESADOS = ["REDE","REDE","REDE","RAMAL","RAMAL","RAMAL",
  "DESPERDICIO","DESPERDICIO","ADUTORA","CLANDESTINA"];

function gerarChamadoAleatorio(){
  const tipoId = TIPOS_PESADOS[Math.floor(Math.random() * TIPOS_PESADOS.length)];
  const t = TIPOS.find(x => x.id === tipoId);
  const porteId = t.fixa ? PORTES[2].id : PORTES[Math.floor(Math.random() * PORTES.length)].id;
  const b = BAIRROS[Math.floor(Math.random() * BAIRROS.length)];
  const lat = b.lat + (Math.random() * 0.01 - 0.005);
  const lng = b.lng + (Math.random() * 0.01 - 0.005);
  const autor = NOMES_FICTICIOS[Math.floor(Math.random() * NOMES_FICTICIOS.length)];

  return criarChamado({tipo: tipoId, porte: porteId, lat, lng, autor, horasAberto: 0});
}

/* Injeta o novo chamado, avisa e atualiza só a tela que estiver visível
   no momento — sem interromper quem estiver no meio de um cadastro. */
function simularChegadaDeChamado(){
  if(estado.chamados.length >= MAXIMO_CHAMADOS_SIMULADOS) return;

  const novo = gerarChamadoAleatorio();
  estado.chamados.push(novo);
  estado.recemChegado = novo.protocolo;
  setTimeout(() => { if(estado.recemChegado === novo.protocolo) estado.recemChegado = null; }, 2600);

  const t = TIPOS.find(x => x.id === novo.tipo);
  avisar("Novo chamado recebido", novo.bairro + " · " + t.nome + " · " + fmt(novo.vazao) + " L/h", "alerta");

  if(estado.vista === "op"){
    pintarPainel(false);
  } else if(estado.telaApp !== "registrar"){
    desenhar();
  }
  // Se a pessoa estiver no meio de um cadastro (telaApp === "registrar"),
  // a tela não é redesenhada para não perder o que já foi preenchido —
  // o aviso flutuante ainda aparece, e a fila é atualizada na próxima troca de aba.
}


const ESTILO_MAPA = [
  {elementType:"geometry", stylers:[{color:"#0C1B27"}]},
  {elementType:"labels.text.stroke", stylers:[{color:"#0B1822"}]},
  {elementType:"labels.text.fill", stylers:[{color:"#7E95A6"}]},
  {featureType:"administrative", elementType:"geometry", stylers:[{color:"#1E374B"}]},
  {featureType:"administrative.neighborhood", elementType:"labels.text.fill", stylers:[{color:"#5B788E"}]},
  {featureType:"poi", stylers:[{visibility:"off"}]},
  {featureType:"road", elementType:"geometry", stylers:[{color:"#162B3C"}]},
  {featureType:"road", elementType:"labels.text.fill", stylers:[{color:"#5B788E"}]},
  {featureType:"road.highway", elementType:"geometry", stylers:[{color:"#22425A"}]},
  {featureType:"transit", stylers:[{visibility:"off"}]},
  {featureType:"water", elementType:"geometry", stylers:[{color:"#16304F"}]},
  {featureType:"water", elementType:"labels.text.fill", stylers:[{color:"#6CADDF"}]}
];

function carregarGoogle(){
  if(!CHAVE_MAPS){ estado.google.falhou = true; return; }
  const s = document.createElement("script");
  s.src = "https://maps.googleapis.com/maps/api/js?key=" + CHAVE_MAPS +
          "&callback=googlePronto&libraries=places&language=pt-BR&region=BR";
  s.async = true;
  s.onerror = () => falharGoogle("sem conexão com o Google Maps");
  document.head.appendChild(s);
  setTimeout(() => { if(!estado.google.pronto) falharGoogle("o Google Maps demorou a responder"); }, 8000);
}
/* o Google só diz o motivo exato no console; aqui a gente captura e traduz */
const MOTIVOS_MAPS = {
  RefererNotAllowedMapError:"o site não está autorizado na chave (restrição de HTTP referrer). Abrindo o arquivo direto do computador (file://) isso sempre falha: use uma chave sem restrição de site, ou hospede a página e autorize o endereço dela",
  ApiNotActivatedMapError:"a Maps JavaScript API não está ativada no projeto dessa chave",
  ApiTargetBlockedMapError:"a chave está restrita e não inclui a Maps JavaScript API (ou a Places API)",
  BillingNotEnabledMapError:"o projeto do Google Cloud está sem faturamento ativo",
  InvalidKeyMapError:"a chave é inválida (confira se foi copiada inteira)",
  MissingKeyMapError:"nenhuma chave foi enviada",
  ExpiredKeyMapError:"a chave expirou",
  OverQuotaMapError:"a cota gratuita acabou",
  RequestDeniedMapError:"o Google negou o pedido"
};
const _consoleErroOriginal = console.error;
console.error = function(...a){
  try{
    const t = a.map(x => String(x)).join(" "), m = t.match(/Google Maps JavaScript API (?:error|warning): (\w+)/);
    if(m && MOTIVOS_MAPS[m[1]]){ estado.google.codigo = m[1]; estado.google.motivo = MOTIVOS_MAPS[m[1]]; }
  }catch(e){}
  return _consoleErroOriginal.apply(console, a);
};
window.gm_authFailure = () => setTimeout(() => falharGoogle("o Google Maps recusou a chave"), 60);

function falharGoogle(motivo){
  if(estado.google.pronto || estado.google.falhou) return;
  estado.google.falhou = true;
  const g = estado.google, detalhe = g.motivo ? ": " + g.motivo : "";
  console.warn("[Água em Dia] Google Maps indisponível: " + motivo + detalhe + " | origem da página: " + location.origin);
  avisar("Mapa esquemático ativado", motivo + detalhe + ". O protótipo segue funcionando.", "alerta");
  desenhar();
}

window.googlePronto = function(){
  estado.google.pronto = true;
  desenhar();
};

/* cria (ou reaproveita) um mapa do Google dentro de um container */
function montarMapaGoogle(container, {zoom, clicavel}){
  const mapa = new google.maps.Map(container, {
    center: CENTRO_RECIFE,
    zoom: zoom,
    styles: ESTILO_MAPA,
    disableDefaultUI: true,
    zoomControl: true,
    gestureHandling: "greedy",
    clickableIcons: false
  });
  if(clicavel){
    mapa.addListener("click", e => marcarLocalRascunho(e.latLng.lat(), e.latLng.lng(), mapa));
  }
  return mapa;
}

/* Silhueta do pino: gota d'água com a ponta para baixo, na caixa 40x52.
   A ponta fica exatamente em (20,49) — é ali que o marcador se ancora. */
const CAMINHO_GOTA = "M20 49C11.5 38.5 4 30.5 4 21a16 16 0 1 1 32 0c0 9.5-7.5 17.5-16 28Z";
/* gotinha interna, usada como miolo */
const CAMINHO_MIOLO = "M20 12.5c0 0-5.6 6.4-5.6 9.7a5.6 5.6 0 0 0 11.2 0c0-3.3-5.6-9.7-5.6-9.7Z";
const CAMINHO_CHECK = "M13.8 21.4l4.2 4.2 8.2-8.6";

/* Escala logarítmica: um vazamento 100x maior não pode virar um pino 100x maior,
   mas 3 L/h e 27.000 L/h precisam ser visivelmente diferentes. */
function tamanhoPino(vazao){
  const t = 26 + Math.log10(Math.max(1, vazao)) * 5;
  return Math.round(Math.max(26, Math.min(50, t)));
}

/* Monta o SVG do pino e devolve como data URI — permite duas cores,
   sombra e miolo, coisa que o marcador de círculo do Google não faz. */
function svgPinoGota(c){
  const cor = CORES[nivel(c)];
  const sel = estado.selecionado === c.protocolo;
  const largura = tamanhoPino(c.vazao);
  const altura = Math.round(largura * 1.3);

  const halo = nivel(c) === "alta"
    ? `<path d="${CAMINHO_GOTA}" fill="${cor}" opacity=".2"
         transform="translate(20 27) scale(1.26) translate(-20 -27)"/>` : "";

  const miolo = c.situacao === "RESOLVIDO"
    ? `<path d="${CAMINHO_CHECK}" fill="none" stroke="#0A1A33" stroke-width="3.6"
         stroke-linecap="round" stroke-linejoin="round"/>`
    : c.situacao === "REPARO"
    ? `<circle cx="20" cy="21" r="6.4" fill="none" stroke="#0A1A33" stroke-width="3"/>
       <path d="M20 17.4v4.2l2.8 1.8" fill="none" stroke="#0A1A33" stroke-width="2.6" stroke-linecap="round"/>`
    : `<path d="${CAMINHO_MIOLO}" fill="#081B2A" opacity=".8"/>`;

  const svg =
    `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 40 52" width="${largura}" height="${altura}">` +
      `<ellipse cx="20" cy="49.5" rx="6.2" ry="2.2" fill="#000" opacity=".35"/>` +
      halo +
      `<path d="${CAMINHO_GOTA}" fill="${cor}" stroke="${sel ? "#EDF2F1" : "#0A1A33"}"` +
        ` stroke-width="${sel ? 3.2 : 2.2}" stroke-linejoin="round"/>` +
      miolo +
    `</svg>`;

  return {url:"data:image/svg+xml;charset=UTF-8," + encodeURIComponent(svg),
          largura, altura};
}

function iconePino(c){
  const g = svgPinoGota(c);
  return {
    url: g.url,
    scaledSize: new google.maps.Size(g.largura, g.altura),
    anchor: new google.maps.Point(g.largura / 2, g.altura)
  };
}

function iconeMinimo(c){
  const sel = estado.selecionado === c.protocolo;
  return {path:google.maps.SymbolPath.CIRCLE, scale: sel ? 9 : 6, fillColor: CORES[nivel(c)], fillOpacity: .9,
          strokeColor: "#FFFFFF", strokeOpacity: sel ? 1 : .75, strokeWeight: sel ? 3 : 1.5};
}
function pintarPinosGoogle(mapa, chave){
  if(!mapa) return;
  const cache = estado.google.pinos[chave] = estado.google.pinos[chave] || {};
  const soRaio = chave === "app" && estado.telaApp === "mapa";
  const lista = soRaio ? estado.chamados.filter(noRaio) : estado.chamados;
  // remove os que não existem mais (ou que saíram do raio de 5 km)
  for(const proto in cache){
    if(!lista.some(c => c.protocolo === proto)){
      cache[proto].setMap(null);
      delete cache[proto];
    }
  }
  for(const c of lista){
    const camada = estado.selecionado === c.protocolo
      ? 99999 : Math.round(Math.min(9000, prioridade(c)));
    if(cache[c.protocolo]){
      cache[c.protocolo].setIcon(soRaio ? iconeMinimo(c) : iconePino(c));
      cache[c.protocolo].setZIndex(camada);
      continue;
    }
    const pino = new google.maps.Marker({
      position:{lat:c.lat, lng:c.lng},
      map: mapa,
      icon: soRaio ? iconeMinimo(c) : iconePino(c),
      zIndex: camada,
      title: c.referencia + " — " + fmt(c.vazao) + " L/h",
      animation: soRaio ? null : google.maps.Animation.DROP
    });
    pino.addListener("click", () => selecionarChamado(c.protocolo));
    cache[c.protocolo] = pino;
  }
}

function marcarLocalRascunho(lat, lng, mapa){
  if(!estado.rascunho) return;
  const b = bairroPorCoordenada(lat, lng);
  estado.rascunho.lat = lat;
  estado.rascunho.lng = lng;
  estado.rascunho.bairro = b.nome;
  if(estado.google.pinoRascunho) estado.google.pinoRascunho.setMap(null);
  const svgRascunho =
    '<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 56 76" width="46" height="62">' +
      '<defs><linearGradient id="pg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#5BC0FA"/><stop offset="1" stop-color="#1B55B3"/></linearGradient>' +
      '<linearGradient id="pd" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stop-color="#3FA8F0"/><stop offset="1" stop-color="#1B55B3"/></linearGradient></defs>' +
      '<ellipse cx="28" cy="71" rx="7" ry="2.6" fill="none" stroke="#5BC0FA" stroke-width="2"><animate attributeName="rx" values="5;20" dur="1.8s" repeatCount="indefinite"/><animate attributeName="ry" values="1.8;6" dur="1.8s" repeatCount="indefinite"/><animate attributeName="opacity" values=".9;0" dur="1.8s" repeatCount="indefinite"/></ellipse>' +
      '<ellipse cx="28" cy="71" rx="6" ry="2.2" fill="#000" opacity=".35"/>' +
      '<path d="M28 71 C14 52 5 42 5 28 a23 23 0 1 1 46 0 c0 14-9 24-23 43Z" fill="url(#pg)" stroke="#fff" stroke-width="3.4" stroke-linejoin="round"/>' +
      '<circle cx="28" cy="28" r="14" fill="#fff"/>' +
      '<path d="M28 17 C28 17 20.5 25.5 20.5 30.5 a7.5 7.5 0 0 0 15 0 C35.5 25.5 28 17 28 17Z" fill="url(#pd)"/>' +
      '<path d="M24.4 31.6 a4 4 0 0 0 3.6 3.2" fill="none" stroke="#fff" stroke-width="1.8" stroke-linecap="round" opacity=".85"/>' +
    '</svg>';
  estado.google.pinoRascunho = new google.maps.Marker({
    position:{lat, lng}, map: mapa, animation: google.maps.Animation.DROP, zIndex: 99999,
    icon:{url:"data:image/svg+xml;charset=UTF-8," + encodeURIComponent(svgRascunho),
          scaledSize:new google.maps.Size(46,62), anchor:new google.maps.Point(23,62)}
  });
  buscarEndereco(lat, lng);
  atualizarRotuloLocal();
}

/* Geocodificação reversa: se a API não estiver habilitada, mantém a rua fictícia. */
function buscarEndereco(lat, lng){
  if(!estado.google.pronto || !google.maps.Geocoder) return;
  try{
    new google.maps.Geocoder().geocode({location:{lat,lng}}, (res, status) => {
      if(status === "OK" && res && res[0] && estado.rascunho && !estado.rascunho.refEditada){
        estado.rascunho.referencia = res[0].formatted_address.split(" - ")[0];
        atualizarRotuloLocal();
      }
    });
  }catch(e){ /* segue com a referência digitada */ }
}

function atualizarRotuloLocal(){
  const r = estado.rascunho;
  if(!r) return;
  const caixa = document.getElementById("resumoLocal");
  if(caixa){
    caixa.innerHTML = r.bairro
      ? `<div class="quadro surge"><div class="linha-dado"><span>Bairro</span><span>${r.bairro}</span></div></div>`
      : "";
  }
  const campo = document.getElementById("campoRef");
  if(campo && !r.refEditada && r.referencia) campo.value = r.referencia;
  const botao = document.getElementById("btnLocal");
  if(botao) botao.disabled = !(r.lat || r.x);
  const instr = document.getElementById("instrucaoMapa");
  if(instr && (r.lat || r.x)) instr.style.display = "none";
}

/* =====================================================================
   MAPA ESQUEMÁTICO (fallback)
   ===================================================================== */
let ajusteSvgCache = null;
function ajusteSvg(){
  if(ajusteSvgCache) return ajusteSvgCache;
  const reg = (xs, ys) => {
    const n = xs.length, mx = xs.reduce((a,b) => a+b,0)/n, my = ys.reduce((a,b) => a+b,0)/n;
    let sxy = 0, sxx = 0; xs.forEach((x,i) => { sxy += (x-mx)*(ys[i]-my); sxx += (x-mx)*(x-mx); });
    const b = sxy/sxx; return {a: my - b*mx, b};
  };
  return ajusteSvgCache = {x: reg(BAIRROS.map(b => b.lng), BAIRROS.map(b => b.x)),
                           y: reg(BAIRROS.map(b => b.lat), BAIRROS.map(b => b.y))};
}
function svgMapa({id, clicavel}){
  const soRaio = id === "mapaAppSvg" && estado.telaApp === "mapa";
  const ordenados = (soRaio ? estado.chamados.filter(noRaio) : [...estado.chamados]).sort((a,b) => prioridade(a) - prioridade(b));
  const pinos = ordenados.map(c => {
    const cor = CORES[nivel(c)];
    const sel = estado.selecionado === c.protocolo;
    if(soRaio){
      return `<g class="pino${sel ? " selecionado" : ""}" onclick="selecionarChamado('${c.protocolo}')" tabindex="0" role="button"
                aria-label="${c.referencia}, ${fmt(c.vazao)} litros por hora" onkeydown="ativarComTeclado(event,'${c.protocolo}')">
          <title>${c.referencia} — ${fmt(c.vazao)} L/h</title>
          <circle cx="${c.x}" cy="${c.y}" r="18" fill="transparent"/>
          <circle cx="${c.x}" cy="${c.y}" r="${sel ? 9 : 6}" fill="${cor}" fill-opacity=".9" stroke="#fff" stroke-opacity="${sel ? 1 : .7}" stroke-width="${sel ? 3 : 1.5}"/>
        </g>`;
    }
    const escala = (tamanhoPino(c.vazao) / 44) * (sel ? 1.15 : 1);
    const onda = nivel(c) === "alta"
      ? `<circle class="onda" cx="${c.x}" cy="${c.y}" r="6" fill="${cor}" opacity=".4"/>` : "";
    const miolo = c.situacao === "RESOLVIDO"
      ? `<path d="${CAMINHO_CHECK}" fill="none" stroke="#0A1A33" stroke-width="3.6"
           stroke-linecap="round" stroke-linejoin="round"/>`
      : c.situacao === "REPARO"
      ? `<circle cx="20" cy="21" r="6.4" fill="none" stroke="#0A1A33" stroke-width="3"/>
         <path d="M20 17.4v4.2l2.8 1.8" fill="none" stroke="#0A1A33" stroke-width="2.6" stroke-linecap="round"/>`
      : `<path d="${CAMINHO_MIOLO}" fill="#081B2A" opacity=".8"/>`;
    return `<g class="pino${sel ? " selecionado" : ""}" onclick="selecionarChamado('${c.protocolo}')"
              tabindex="0" role="button" aria-label="${c.referencia}, ${fmt(c.vazao)} litros por hora"
              onkeydown="ativarComTeclado(event,'${c.protocolo}')">
        ${onda}<title>${c.referencia} — ${fmt(c.vazao)} L/h</title>
        <g transform="translate(${c.x} ${c.y}) scale(${escala.toFixed(2)}) translate(-20 -49)">
          <ellipse cx="20" cy="49.5" rx="6.2" ry="2.2" fill="#000" opacity=".35"/>
          <path class="corpo" d="${CAMINHO_GOTA}" fill="${cor}"
                stroke="${sel ? "#EDF2F1" : "#0A1A33"}" stroke-width="${sel ? 3.2 : 2.2}"
                stroke-linejoin="round"/>
          ${miolo}
        </g></g>`;
  }).join("");

  const r = estado.rascunho;
  const marcaRascunho = (r && r.x)
    ? `<g transform="translate(${r.x} ${r.y}) scale(0.72) translate(-20 -49)">
         <ellipse cx="20" cy="49.5" rx="6.2" ry="2.2" fill="#000" opacity=".35"/>
         <path d="${CAMINHO_GOTA}" fill="#EDF2F1" stroke="#6CADDF" stroke-width="3"/>
         <path d="${CAMINHO_MIOLO}" fill="#6CADDF"/></g>` : "";

  const nomes = BAIRROS.map(b =>
    `<text class="rotulo-bairro" x="${b.x}" y="${b.y + 17}" text-anchor="middle">${b.nome}</text>`).join("");

  return `<svg class="mapa" id="${id}" viewBox="0 0 560 800"
      ${clicavel ? `onclick="cliqueNoSvg(event,'${id}')"` : ""} role="img"
      aria-label="Mapa esquemático do Recife com os chamados de vazamento">
    <rect width="560" height="800" fill="#1A4F86"/>
    <path d="M520,0 L500,120 L470,260 L505,300 L480,360 L500,430 L470,470 L500,560 L470,700 L440,800 L560,800 L560,0 Z" fill="#3D7DBA"/>
    <path d="M0,0 L520,0 L500,120 L470,260 L505,300 L480,360 L500,430 L470,470 L500,560 L470,700 L440,800 L0,800 Z" fill="#235B93"/>
    <ellipse cx="472" cy="412" rx="34" ry="46" fill="#4A8AC6" opacity=".85"/>
    <path class="rio" style="stroke-width:9" d="M20,352 C110,372 180,340 268,352 C350,364 410,320 470,300"/>
    <path class="rio" style="stroke-width:6" d="M296,20 C322,110 366,180 424,246 C446,270 460,288 470,300"/>
    <path class="rio" style="stroke-width:6" d="M20,616 C120,596 210,566 300,522 C376,486 432,448 468,414"/>
    <path class="rio" style="stroke-width:3;stroke-opacity:.35" d="M160,352 C176,420 196,470 212,540"/>
    <path class="rio" style="stroke-width:3;stroke-opacity:.35" d="M330,356 C348,406 358,452 366,506"/>
    <g stroke="#8ABAE1" stroke-width="1.2" opacity=".7">
      <path d="M60,140 L470,120 M40,240 L480,225 M30,470 L470,455 M40,590 L455,575 M50,700 L448,690"/>
      <path d="M140,20 L120,790 M250,20 L236,790 M360,20 L352,790 M440,30 L452,780"/>
    </g>
    ${nomes}${marcaRascunho}${soRaio ? raioSvg() : ""}${pinos}
  </svg>`;
}

function raioSvg(){
  const A = ajusteSvg(), c0 = centroMapa();
  const cx = A.x.a + A.x.b * c0.lng, cy = A.y.a + A.y.b * c0.lat;
  const pxPorMetro = Math.abs(A.x.b) / (111320 * Math.cos(c0.lat * Math.PI / 180));
  const r = RAIO_MAPA_M * pxPorMetro;
  return (c0.real ? `<circle cx="${cx.toFixed(1)}" cy="${cy.toFixed(1)}" r="7" fill="#4DA3FF" stroke="#fff" stroke-width="3"/>` : "");
}

function cliqueNoSvg(evt, idSvg){
  if(!estado.rascunho) return;
  const svg = document.getElementById(idSvg);
  const caixa = svg.getBoundingClientRect();
  const x = Math.round((evt.clientX - caixa.left) / caixa.width * 560);
  const y = Math.round((evt.clientY - caixa.top) / caixa.height * 800);
  const b = bairroPorPosicao(x,y);
  estado.rascunho.x = x;
  estado.rascunho.y = y;
  estado.rascunho.bairro = b.nome;
  if(!estado.rascunho.refEditada) estado.rascunho.referencia = ruaFicticia() + " — " + b.nome;
  desenhar();
}

/* bloco de mapa: Google quando disponível, SVG quando não */
function blocoMapa({idSvg, idGoogle, clicavel, instrucao}){
  const g = estado.google;
  if(g.pronto){
    return `<div class="mapa-caixa">
      <div class="selo-mapa"><span class="ponto"></span>Google Maps · Recife</div>
      <div class="mapa-google" id="${idGoogle}"></div>
      ${instrucao ? `<div class="instrucao-mapa" id="instrucaoMapa">${ICO.alfinete}${instrucao}</div>` : ""}
    </div>`;
  }
  if(!g.falhou){
    return `<div class="mapa-caixa" style="height:300px">
      <div class="carregando-mapa"><div style="text-align:center">
        <div class="loader-agua"><i></i><i></i><i></i></div>Carregando o mapa do Recife…</div></div>
    </div>`;
  }
  return `<div class="mapa-caixa">
    <div class="selo-mapa"><span class="ponto off"></span>Mapa esquemático · ${estado.google.codigo ? "Google: " + estado.google.codigo.replace(/MapError$/,"") : (navigator.onLine ? "Google Maps indisponível" : "sem internet")}</div>
    ${svgMapa({id:idSvg, clicavel})}
    ${instrucao ? `<div class="instrucao-mapa" id="instrucaoMapa">${ICO.alfinete}${instrucao}</div>` : ""}
  </div>`;
}

/* depois de injetar o HTML, instancia os mapas do Google */
function ativarMapas(){
  const g = estado.google;
  if(!g.pronto) return;
  const alvoApp = document.getElementById("mapaAppGoogle");
  if(alvoApp){
    g.mapaApp = montarMapaGoogle(alvoApp, {zoom: estado.telaApp === "registrar" ? 12 : 11.5,
                                           clicavel: estado.telaApp === "registrar"});
    pintarPinosGoogle(g.mapaApp, "app");
    if(estado.telaApp === "mapa"){
      const c0 = centroMapa();
      // círculo invisível: serve só para enquadrar o mapa nos 3 km (nada é desenhado)
      const limite = new google.maps.Circle({center:{lat:c0.lat, lng:c0.lng}, radius:RAIO_MAPA_M});
      g.mapaApp.fitBounds(limite.getBounds(), 8);
      if(c0.real) new google.maps.Marker({map:g.mapaApp, position:{lat:c0.lat, lng:c0.lng}, clickable:false, zIndex:100000,
        icon:{path:google.maps.SymbolPath.CIRCLE, scale:8, fillColor:"#4DA3FF", fillOpacity:1, strokeColor:"#fff", strokeWeight:3}});
    }
    if(estado.telaApp === "registrar"){
      g.pinoRascunho = null;
      if(estado.rascunho && estado.rascunho.lat){
        marcarLocalRascunho(estado.rascunho.lat, estado.rascunho.lng, g.mapaApp);
      }
    }
  }
  const alvoOp = document.getElementById("mapaOpGoogle");
  if(alvoOp){
    g.mapaOp = montarMapaGoogle(alvoOp, {zoom:11.5, clicavel:false});
    pintarPinosGoogle(g.mapaOp, "op");
  }
}

/* Ativa um elemento clicável (div com role="button") pelo teclado.
   Enter já é nativo em muitos casos, mas Espaço precisa de preventDefault
   para não rolar a página — e é o padrão que leitores de tela esperam. */
function ativarComTeclado(evt, protocolo){
  if(evt.key === "Enter" || evt.key === " " || evt.key === "Spacebar"){
    evt.preventDefault();
    selecionarChamado(protocolo);
  }
}

function selecionarChamado(protocolo){
  estado.selecionado = estado.selecionado === protocolo ? null : protocolo;
  if(estado.google.pronto){
    pintarPinosGoogle(estado.google.mapaApp, "app");
    pintarPinosGoogle(estado.google.mapaOp, "op");
  }
  if(estado.vista === "op"){
    pintarFila();
    const alvo = document.getElementById("item-" + protocolo);
    if(alvo) alvo.scrollIntoView({block:"nearest", behavior:"smooth"});
  } else if(estado.telaApp === "mapa" && estado.google.pronto){
    const folha = document.getElementById("folhaMapa");
    if(folha) folha.innerHTML = folhaChamado();
  } else {
    desenhar();
  }
}

/* =====================================================================
   APP DO CIDADÃO
   ===================================================================== */
function navegarApp(tela){
  estado.telaApp = tela;
  if(tela === "registrar" && !estado.rascunho){
    estado.rascunho = {tipo:null, porte:null, lat:null, lng:null, x:null, y:null,
                       bairro:null, referencia:"", refEditada:false, descricao:"", foto:null};
    estado.passo = 1;
  }
  if(tela === "chamados") tela = estado.telaApp = "perfil";
  if(tela === "perfil"){
    estado.novosNoPerfil = new Set(estado.chamados.filter(c => c.meu && c.ultimaSituacaoVista !== c.situacao).map(c => c.protocolo));
    marcarChamadosComoVistos();
  }
  desenhar();
}

/* ---------- badge de notificação (mudanças de status ainda não vistas) ---------- */
function contarNotificacoesNaoVistas(){
  return estado.chamados.filter(c =>
    c.meu && c.ultimaSituacaoVista !== c.situacao
  ).length;
}

function marcarChamadosComoVistos(){
  const havia = contarNotificacoesNaoVistas() > 0;
  estado.chamados.forEach(c => {
    if(c.meu) c.ultimaSituacaoVista = c.situacao;
  });
  if(havia && API.temToken()) API.marcarVistos().catch(() => {});
}

function pintarNav(){
  const naoVistas = contarNotificacoesNaoVistas();
  const itens = [
    {id:"mapa", ico:ICO.mapa, txt:"Mapa"},
    {id:"registrar", central:true, ico:ICO.mais, txt:"Registrar"},
    {id:"perfil", ico:ICO.perfil, txt:"Perfil", badge:naoVistas}
  ];
  document.getElementById("navApp").innerHTML = itens.map(i => i.central
    ? `<button class="central" onclick="navegarApp('registrar')" aria-label="Registrar vazamento">${i.ico}</button>`
    : `<button${i.zap ? ' class="nav-zap"' : ""} aria-current="${estado.telaApp === i.id}" onclick="navegarApp('${i.id}')">
         <span class="nav-icone">${i.ico}${
           i.badge ? `<span class="badge-notif" aria-hidden="true">${i.badge > 9 ? "9+" : i.badge}</span>` : ""
         }</span><span>${i.txt}${
           i.badge ? `<span class="apenas-leitor-tela"> — ${i.badge} atualização(ões) não vista(s)</span>` : ""
         }</span></button>`).join("");
}

function telaApp(){
  if(estado.telaApp === "mapa")     return telaMapa();
  if(estado.telaApp === "perfil")   return telaPerfil();
  return telaRegistrar();
}

/* ---------- mapa inicial: só o raio de 5 km, sem listas ---------- */
const RAIO_MAPA_M = 3000;
function centroMapa(){
  const L = estado.local;
  if(L.status === "ok" && distanciaMetros(L.lat, L.lng, CENTRO_RECIFE.lat, CENTRO_RECIFE.lng) <= 60000)
    return {lat:L.lat, lng:L.lng, real:true};
  return {lat:CENTRO_RECIFE.lat, lng:CENTRO_RECIFE.lng, real:false};   // fora do Recife ou sem GPS: centro da cidade
}
function chamadosNoRaio(){
  const c0 = centroMapa();
  return estado.chamados.filter(c => {
    c.distancia = distanciaMetros(c0.lat, c0.lng, c.lat, c.lng);
    return !c.meu && c.distancia <= RAIO_MAPA_M;   // os chamados da própria pessoa ficam no Perfil
  });
}
function noRaio(c){
  const c0 = centroMapa();
  return !c.meu && distanciaMetros(c0.lat, c0.lng, c.lat, c.lng) <= RAIO_MAPA_M;
}
function folhaChamado(){
  const c = estado.chamados.find(x => x.protocolo === estado.selecionado);
  if(!c || !noRaio(c)) return "";
  c.distancia = distanciaMetros(centroMapa().lat, centroMapa().lng, c.lat, c.lng);
  const tp = TIPOS.find(x => x.id === c.tipo);
  const rot = {ABERTO:"aberto", REPARO:"reparo", RESOLVIDO:"resolvido"}[c.situacao];
  const txt = {ABERTO:"Na fila", REPARO:"Equipe a caminho", RESOLVIDO:"Reparado"}[c.situacao];
  return `<div class="folha-mapa surge" role="group" aria-label="Detalhes do chamado selecionado">
    ${blocoFotoChamado(c, "mini")}
    <div class="folha-corpo">
      <b>${tp.nome}</b>
      <span class="folha-ref">${c.referencia}</span>
      <span class="folha-meta">${fmt(c.vazao)} L/h · a ${fmtDistancia(c.distancia)} · aberto há ${fmtHoras(horasEmAberto(c))}</span>
      <div class="folha-linha"><span class="selo ${rot}">${txt}</span>${textoApoios(c) ? `<span class="folha-apoios">✓ ${textoApoios(c)}</span>` : ""}</div>
      <div class="folha-acoes">
        ${podeApoiar(c) ? `<button class="btn-apoio" onclick="apoiarChamado('${c.protocolo}')">Eu também vi</button>` : ""}
        <button class="btn-detalhes" onclick="abrirDetalheChamado('${c.protocolo}')">Ver detalhes</button></div>
    </div>
    <button class="folha-fechar" onclick="selecionarChamado('${c.protocolo}')" aria-label="Fechar">×</button>
  </div>`;
}
function telaMapa(){
  return `
    <div class="mapa-cheio">
      ${blocoMapa({idSvg:"mapaAppSvg", idGoogle:"mapaAppGoogle", clicavel:false})}
      <button class="btn-minha-loc" onclick="detectarLocalizacao(false)" aria-label="Usar minha localização" title="Usar minha localização">${ICO.alvo}</button>
      <div id="folhaMapa">${folhaChamado()}</div>
    </div>
    <p class="dica-mapa">Ocorrências em até 3 km · toque num ponto se quiser ver mais</p>`;
}

/* =====================================================================
   PERFIL DA YE
   ===================================================================== */
function calcularPerfilYe(){
  const meus = estado.chamados.filter(c => c.meu);
  return {
    registros: meus.length,
    resolvidos: meus.filter(c => c.situacao === "RESOLVIDO").length,
    volume: meus.reduce((s,c) => s + volumePerdido(c), 0)
  };
}

function cartaoPerfilYe(){
  const p = calcularPerfilYe();

  return `<div class="cartao-perfil surge">
    <div class="perfil-cabeca">
      <label class="avatar-troca" title="Trocar foto de perfil">
        <span class="avatar avatar-grande">${miolioAvatar(USUARIO.nome, iniciaisNomeSimples(USUARIO.nome))}</span>
        <span class="avatar-cam" aria-hidden="true">${ICO.camera}</span>
        <input type="file" accept="image/*" hidden onchange="trocarFotoPerfil(this)" aria-label="Trocar foto de perfil">
      </label>
      <div>
        <div class="perfil-nome">${escaparHtml(USUARIO.nome)}</div>
        <div class="perfil-sub">Sua contribuição para a rede</div>
        ${USUARIO.foto ? `<button class="avatar-remover" onclick="removerFotoPerfil()">Remover foto</button>` : `<span class="avatar-dica">Toque na foto para trocar</span>`}
      </div>
    </div>
    <dl class="perfil-dados">
      ${USUARIO.usuario ? `<div><dt>Usuário</dt><dd>@${escaparHtml(USUARIO.usuario)}</dd></div>` : ""}
      <div><dt>E-mail</dt><dd>${escaparHtml(USUARIO.email || "—")}</dd></div>
      <div><dt>Celular</dt><dd>${escaparHtml(USUARIO.tel || "—")}</dd></div>
      <div><dt>CPF</dt><dd>${escaparHtml(USUARIO.cpfMasc || "—")}</dd></div>
    </dl>
    <div class="perfil-stats">
      <div class="perfil-stat"><span class="v">${p.registros}</span><span class="r">registrados</span></div>
      <div class="perfil-stat"><span class="v">${p.resolvidos}</span><span class="r">resolvidos</span></div>
      <div class="perfil-stat"><span class="v">${fmtLitros(p.volume)}</span><span class="r">mapeados</span></div>
    </div>
  </div>`;
}

function telaPerfil(){
  const meus = estado.chamados.filter(c => c.meu);
  const f = estado.filtroPerfil || "todos";
  const cont = {todos:meus.length,
    andamento:meus.filter(c => c.situacao !== "RESOLVIDO").length,
    resolvido:meus.filter(c => c.situacao === "RESOLVIDO").length};
  const ordem = {REPARO:0, ABERTO:1, RESOLVIDO:2};
  const lista = meus.filter(c => f === "todos" || (f === "andamento" ? c.situacao !== "RESOLVIDO" : c.situacao === "RESOLVIDO"))
    .sort((a,b) => ordem[a.situacao] - ordem[b.situacao]);
  const litros = meus.reduce((s,c) => s + volumePerdido(c), 0);
  const novos = estado.novosNoPerfil ? estado.novosNoPerfil.size : 0;
  const filtros = [["todos","Todos"],["andamento","Em andamento"],["resolvido","Resolvidos"]].map(([id,nome]) =>
    `<button class="chip-filtro" role="tab" aria-selected="${f === id}" onclick="filtrarPerfil('${id}')">${nome}<i>${cont[id]}</i></button>`).join("");
  let corpo;
  if(!meus.length){
    corpo = `<div class="vazio surge">
      <div class="circulo">${ICO.gota}</div>
      <strong>Nenhum chamado seu ainda</strong>
      Viu água correndo na rua, cano estourado ou hidrante aberto?
      Registre em menos de um minuto.
      <div class="acoes" style="margin-top:18px">
        <button class="btn btn-1" onclick="navegarApp('registrar')">Registrar agora</button>
      </div></div>`;
  } else if(!lista.length){
    corpo = `<div class="vazio surge" style="padding:28px 20px"><strong>Nada por aqui</strong>Nenhum chamado nesse filtro.</div>`;
  } else {
    corpo = `<div class="escada">${lista.map(c => cartaoChamado(c).replace('class="cartao com-foto"',
      'class="cartao com-foto' + (estado.novosNoPerfil && estado.novosNoPerfil.has(c.protocolo) ? ' atualizado' : '') + '"')).join("")}</div>`;
  }
  return `<div class="conteudo-app">
    ${cartaoPerfilYe()}
    ${novos ? `<div class="faixa-cidade surge" style="margin:0 0 14px">🔔 <b>${novos} chamado(s)</b> com novidade desde sua última visita.</div>` : ""}
    ${meus.length ? `<div class="faixa-cidade surge" style="margin:0 0 14px">
      Seus chamados já apontaram <b class="num">${fmtLitros(litros)}</b> de perda — o consumo mensal de
      <b>${familias(litros) < 1 ? "menos de 1 família" : fmt(familias(litros)) + " famílias"}</b>.</div>` : ""}
    <div class="titulo-secao"><h3>Meus chamados</h3><span>${meus.length}</span></div>
    ${meus.length ? `<div class="chips-filtro" role="tablist" aria-label="Filtrar chamados">${filtros}</div>` : ""}
    ${corpo}
    <button class="btn btn-2 btn-sair" onclick="sairDaConta()">Sair da conta</button>
  </div>`;
}

function filtrarPerfil(id){
  estado.filtroPerfil = id;
  const topo = document.getElementById("foneCorpo").scrollTop;
  desenhar();
  document.getElementById("foneCorpo").scrollTop = topo;
}

function cartaoChamado(c){
  const t = TIPOS.find(t => t.id === c.tipo);
  const rot = {ABERTO:"aberto", REPARO:"reparo", RESOLVIDO:"resolvido"}[c.situacao];
  const txt = {ABERTO:"Na fila", REPARO:"Equipe a caminho", RESOLVIDO:"Reparado"}[c.situacao];
  return `<div class="cartao com-foto" style="border-left-color:${CORES[nivel(c)]}">
    ${blocoFotoChamado(c)}
    <div class="cartao-corpo">
      <button class="titulo-chamado" onclick="selecionarChamado('${c.protocolo}')">${t.nome}</button>
      <div class="meta">${c.referencia}</div>
      <div class="meta">${c.protocolo} · aberto há ${fmtHoras(horasEmAberto(c))}${
        c.distancia != null && estado.local.status === "ok"
          ? ` · <span class="distancia">a ${fmtDistancia(c.distancia)} de você</span>` : ""}</div>
      ${textoApoios(c) ? `<div class="meta apoios">✓ ${textoApoios(c)}</div>` : ""}
      <div class="rodape">
        <span class="perda">${fmt(c.vazao)} L/h · ${fmtLitros(volumePerdido(c))}</span>
        <span style="display:flex;align-items:center;gap:7px">
          <span class="selo ${rot}">${txt}</span>
          <button class="botao-detalhe" onclick="event.stopPropagation();compartilharProtocolo('${c.protocolo}')"
                  aria-label="Compartilhar protocolo ${c.protocolo}">${ICO.compartilhar}</button>
        </span>
      </div>
      ${c.descricao ? `<p class="relato">${escaparHtml(c.descricao)}”</p>` : ""}
      ${c.meu ? blocoTimeline(c) + blocoAvaliacao(c) : ""}
    </div>
  </div>`;
}

/* ---------- fluxo de registro ---------- */
function telaRegistrar(){
  if(estado.passo === 5) return telaRecibo();
  if(estado.duplicata) return `<div class="progresso">${[1,2,3,4].map(n => `<i class="${n <= 3 ? "feito" : ""}"></i>`).join("")}</div>` + passoDuplicata();
  const barras = [1,2,3,4].map(n =>
    `<i class="${n <= estado.passo ? "feito" : ""}"></i>`).join("");
  let corpo;
  if(estado.passo === 1) corpo = passoTipo();
  else if(estado.passo === 2) corpo = passoPorte();
  else if(estado.passo === 3) corpo = passoLocal();
  else corpo = passoConfirmar();
  return `<div class="progresso">${barras}</div>${corpo}`;
}

/* =====================================================================
   IDENTIFICAR O VAZAMENTO PELA FOTO (Gemini com visão)
   A pessoa só envia a foto; o modelo sugere tipo e tamanho e ela confirma
   ou corrige. A chave da API fica só no localStorage deste navegador.
   Sem chave ou sem internet, o registro manual continua funcionando.
   ===================================================================== */
function lerFotoReduzida(arq, max){
  return new Promise((ok, erro) => {
    const leitor = new FileReader();
    leitor.onerror = () => erro(new Error("leitura"));
    leitor.onload = () => {
      const img = new Image();
      img.onerror = () => erro(new Error("imagem"));
      img.onload = () => {
        const k = Math.min(1, max / Math.max(img.width, img.height));
        const c = document.createElement("canvas");
        c.width = Math.round(img.width * k); c.height = Math.round(img.height * k);
        c.getContext("2d").drawImage(img, 0, 0, c.width, c.height);
        ok(c.toDataURL("image/jpeg", .82));
      };
      img.src = leitor.result;
    };
    leitor.readAsDataURL(arq);
  });
}

function escolherFotoIdentificar(input){
  const arq = input.files && input.files[0];
  input.value = "";
  if(!arq) return;
  if(!arq.type.startsWith("image/")){ avisar("Arquivo inválido", "Escolha uma imagem (foto).", "alerta"); return; }
  lerFotoReduzida(arq, 900).then(url => {
    if(!estado.rascunho) return;
    estado.rascunho.foto = url;
    estado.rascunho.autoIA = null;
    estado.ident = {fase: CONFIG.iaDisponivel ? "analisando" : "erro", resultado:null,
      erro: CONFIG.iaDisponivel ? "" : "A identificação por foto não está ativada neste servidor. Escolha o tipo manualmente."};
    desenhar();
    if(CONFIG.iaDisponivel) analisarFoto();
  }).catch(() => avisar("Foto", "Não consegui abrir essa imagem. Tente outra.", "alerta"));
}


async function analisarFoto(){
  const id = estado.ident;
  const falha = (fase, msg) => { if(estado.ident === id){ id.fase = fase; id.erro = msg; desenhar(); } };
  try{
    const foto = await (await fetch(estado.rascunho.foto)).blob();
    const r = await API.identificarFoto(foto);            // o servidor chama o Gemini; a chave nunca vem para o navegador
    const tipoOk = TIPOS.some(x => x.id === r.tipo) || r.tipo === "NENHUM";
    if(!tipoOk || !PORTES.some(p => p.id === r.porte)) return falha("erro", "A resposta veio num formato inesperado.");
    if(estado.ident !== id) return;       // a pessoa cancelou enquanto analisava
    id.fase = "resultado"; id.resultado = r; desenhar();
  }catch(e){
    falha("erro", e instanceof API.ErroApi && e.status ? e.message : "Não consegui analisar a foto agora. Escolha o tipo manualmente.");
  }
}

function confirmarIdentificacao(){
  const r = estado.ident && estado.ident.resultado; if(!r) return;
  const t = TIPOS.find(x => x.id === r.tipo);
  estado.rascunho.tipo = r.tipo;
  estado.rascunho.autoIA = {confianca:r.confianca, motivo:r.motivo};
  estado.ident = null;
  escolherPorte(t.fixa ? "MEDIO" : r.porte);
}
function corrigirManual(){
  const r = estado.ident && estado.ident.resultado;
  if(r && r.tipo !== "NENHUM") estado.rascunho.tipo = r.tipo;      // já deixa o tipo sugerido marcado
  estado.ident = null; desenhar();
}
function cancelarIdentificar(){
  estado.ident = null;
  if(estado.rascunho){ estado.rascunho.foto = null; estado.rascunho.autoIA = null; }
  desenhar();
}

function passoIdentificar(){
  const id = estado.ident, r = estado.rascunho;
  const previa = `<div class="foto-previa ident-previa${id.fase === "analisando" ? " analisando" : ""}"><img src="${r.foto}" alt="Foto enviada"></div>`;
  const trocar = `<label class="btn btn-2 ident-trocar">Enviar outra foto
      <input type="file" accept="image/*" hidden onchange="escolherFotoIdentificar(this)"></label>`;
  let corpo = "";
  if(id.fase === "analisando"){
    corpo = `<div class="ident-status" role="status"><div class="loader-agua"><i></i><i></i><i></i></div>
        <b>Analisando a foto…</b><span>Procurando o tipo e o tamanho do vazamento.</span></div>
      <div class="acoes"><button class="btn btn-2" onclick="cancelarIdentificar()">Cancelar</button></div>`;
  } else if(id.fase === "erro"){
    corpo = `<div class="quadro"><p style="margin:0;font-size:13.5px">${id.erro}</p></div>
      <div class="acoes"><button class="btn btn-2" onclick="corrigirManual()">Escolher manualmente</button>
        <button class="btn btn-1" onclick="estado.ident.fase='analisando';desenhar();analisarFoto()">Tentar de novo</button></div>`;
  } else {
    const x = id.resultado;
    if(x.tipo === "NENHUM"){
      corpo = `<div class="quadro"><b>Não encontrei vazamento nessa foto.</b>
          <p style="margin:6px 0 0;font-size:13px;color:var(--cinza)">${escaparHtml(x.motivo || "")}</p></div>
        ${trocar}
        <div class="acoes"><button class="btn btn-2" onclick="corrigirManual()">Escolher manualmente</button></div>`;
    } else {
      const t = TIPOS.find(y => y.id === x.tipo), p = PORTES.find(y => y.id === x.porte);
      const pct = Math.round(x.confianca * 100), baixa = x.confianca < .55;
      const vazao = t.fixa ? t.fixa : Math.round(p.lh * t.mult);
      corpo = `<div class="quadro destaque ident-resultado">
          <span class="rotulo-mini">O sistema identificou</span>
          <div class="linha-dado"><span>Tipo</span><span>${t.nome}</span></div>
          <div class="linha-dado"><span>Tamanho</span><span>${t.fixa ? "estimado por consumo" : p.nome}</span></div>
          <div class="linha-dado"><span>Perda estimada</span><span>${fmt(vazao)} L/h</span></div>
          <div class="ident-conf"><span>Confiança</span><div class="ident-barra"><i style="width:${pct}%"></i></div><b class="num">${pct}%</b></div>
          <p class="ident-motivo">${escaparHtml(x.motivo || "")}</p>
          ${baixa ? `<p class="ident-aviso">Não tenho muita certeza. Confira antes de continuar.</p>` : ""}
        </div>
        <div class="acoes"><button class="btn btn-2" onclick="corrigirManual()">Corrigir</button>
          <button class="btn btn-1" onclick="confirmarIdentificacao()">Está certo, continuar</button></div>
        ${trocar}`;
    }
  }
  return `<div class="passo surge">
    <span class="eyebrow">PASSO 1 DE 4 · IDENTIFICAÇÃO PELA FOTO</span>
    <h3>${id.fase === "resultado" ? "Confira o que encontramos" : "Enviando sua foto"}</h3>
    ${previa}${corpo}
  </div>`;
}

function passoTipo(){
  if(estado.ident) return passoIdentificar();
  const ops = TIPOS.map(t => `
    <button class="opcao" aria-pressed="${estado.rascunho.tipo === t.id}" onclick="escolherTipo('${t.id}')">
      <span class="marca-tipo">${ICO[t.ico]}</span>
      <span><b>${t.nome}</b><span>${t.desc}</span></span>
    </button>`).join("");
  return `<div class="passo surge">
    <span class="eyebrow">PASSO 1 DE 4</span>
    <h3>O que você está vendo?</h3>
    <label class="foto-adicionar foto-ident">
      <span class="foto-icone">${ICO.camera}</span>
      <span class="texto"><b>Só enviar a foto</b><span>O sistema identifica o tipo e o tamanho do vazamento para você</span></span>
      <input type="file" accept="image/*" capture="environment" hidden onchange="escolherFotoIdentificar(this)">
    </label>
    <div class="auth-ou"><span>ou escolha manualmente</span></div>
    <div class="escada">${ops}</div>
    <div class="acoes"><button class="btn btn-2" onclick="cancelar()">Cancelar</button></div>
  </div>`;
}

function passoPorte(){
  const t = TIPOS.find(t => t.id === estado.rascunho.tipo);
  if(t.fixa){
    return `<div class="passo surge">
      <span class="eyebrow">PASSO 2 DE 4</span>
      <h3>Ligação clandestina</h3>
      <p class="apoio">Aqui não dá para medir pelo jato.</p>
      <div class="quadro">
        <p style="margin:0;font-size:13.5px;color:#C3D4DA">A perda é o consumo que ninguém
          registra. O sistema usa uma estimativa média de <b class="num">800 L/h</b> por ligação
          até a equipe fiscalizar no local.</p>
      </div>
      <div class="acoes">
        <button class="btn btn-2" onclick="voltarPasso()">Voltar</button>
        <button class="btn btn-1" onclick="escolherPorte('MEDIO')">Continuar</button>
      </div>
    </div>`;
  }
  const ops = PORTES.map(p => {
    const escala = p.barras.map(h => h
      ? `<i style="height:${h}px"></i>` : `<i style="height:3px;opacity:.2"></i>`).join("");
    return `<button class="opcao" aria-pressed="${estado.rascunho.porte === p.id}"
              onclick="escolherPorte('${p.id}')">
      <span class="marca-tipo"><span class="gota-escala">${escala}</span></span>
      <span><b>${p.nome}</b><span>${p.desc}</span></span>
      <span class="lh">${fmt(p.lh * t.mult)} L/h</span>
    </button>`;
  }).join("");
  return `<div class="passo surge">
    <span class="eyebrow">PASSO 2 DE 4</span>
    <h3>Qual o tamanho do vazamento?</h3>
    <p class="apoio">Estimativa já ajustada para ${t.nome.toLowerCase()}.</p>
    <div class="escada">${ops}</div>
    <div class="acoes"><button class="btn btn-2" onclick="voltarPasso()">Voltar</button></div>
  </div>`;
}

/* =====================================================================
   BUSCA DE ENDEREÇO E CEP
   Três fontes, na ordem: CEP digitado, sugestões do Google Places e,
   sem internet, a lista local de bairros do Recife.
   ===================================================================== */
let temporizadorBusca = null;

const ehCep = t => /^\d{5}-?\d{3}$/.test(t.trim());
const soDigitos = t => t.replace(/\D/g, "");
function formatarCep(t){
  const d = soDigitos(t);
  return d.length === 8 ? d.slice(0,5) + "-" + d.slice(5) : t;
}

function aoDigitarBusca(valor){
  estado.busca.texto = valor;
  clearTimeout(temporizadorBusca);
  if(valor.trim().length < 3){
    estado.busca.sugestoes = [];
    estado.busca.carregando = false;
    pintarSugestoes();
    return;
  }
  estado.busca.carregando = true;
  pintarSugestoes();
  temporizadorBusca = setTimeout(() => buscarSugestoes(valor), 280);
}

function buscarSugestoes(texto){
  const t = texto.trim();
  if(t !== estado.busca.texto.trim()) return;

  if(ehCep(t)){
    mostrarSugestoes([{tipo:"cep", icone:"carta", titulo:"CEP " + formatarCep(t),
                       sub:"Buscar este endereço", valor:soDigitos(t)}]);
    return;
  }

  if(estado.google.pronto && window.google && google.maps.places
     && google.maps.places.AutocompleteService){
    try{
      new google.maps.places.AutocompleteService().getPlacePredictions({
        input: t,
        componentRestrictions:{country:"br"},
        location: new google.maps.LatLng(CENTRO_RECIFE.lat, CENTRO_RECIFE.lng),
        radius: 30000
      }, (previsoes, status) => {
        if(status === "OK" && previsoes && previsoes.length){
          mostrarSugestoes(previsoes.slice(0,5).map(p => ({
            tipo:"lugar", icone:"alfinete",
            titulo: p.structured_formatting ? p.structured_formatting.main_text : p.description,
            sub: p.structured_formatting ? p.structured_formatting.secondary_text : "",
            valor: p.place_id
          })));
        } else {
          mostrarSugestoes(sugestoesLocais(t));
        }
      });
      return;
    }catch(e){ /* cai para a lista local */ }
  }
  mostrarSugestoes(sugestoesLocais(t));
}

/* Sem Google: sugere bairros do Recife e deixa o texto virar referência. */
function sugestoesLocais(texto){
  const alvo = texto.toLowerCase();
  const achados = BAIRROS
    .filter(b => b.nome.toLowerCase().includes(alvo))
    .slice(0,4)
    .map(b => ({tipo:"bairro", icone:"bairro", titulo:b.nome,
                sub:"Bairro do Recife", valor:b.nome}));
  achados.push({tipo:"texto", icone:"lupa", titulo:'Usar "' + texto + '"',
                sub:"e marcar o ponto tocando no mapa", valor:texto});
  return achados;
}

function mostrarSugestoes(lista){
  estado.busca.sugestoes = lista;
  estado.busca.carregando = false;
  pintarSugestoes();
}

/* Redesenha só a lista, para o campo não perder o foco enquanto digita. */
function pintarSugestoes(){
  const caixa = document.getElementById("listaSugestoes");
  if(!caixa) return;
  const roda = document.getElementById("rodaBusca");
  if(roda) roda.style.display = estado.busca.carregando ? "block" : "none";

  if(!estado.busca.sugestoes.length){ caixa.innerHTML = ""; return; }
  caixa.innerHTML = `<div class="sugestoes">` + estado.busca.sugestoes.map((sg,i) =>
    `<button class="sugestao" onclick="escolherSugestao(${i})">
       <span class="icone">${ICO[sg.icone]}</span>
       <span><b>${sg.titulo}</b>${sg.sub ? `<span>${sg.sub}</span>` : ""}</span>
     </button>`).join("") + `</div>`;
}

function escolherSugestao(indice){
  const sg = estado.busca.sugestoes[indice];
  if(!sg) return;
  estado.busca.sugestoes = [];
  pintarSugestoes();

  if(sg.tipo === "cep")        resolverCep(sg.valor);
  else if(sg.tipo === "lugar") resolverLugar(sg.valor, sg.titulo);
  else if(sg.tipo === "bairro")irParaBairro(sg.valor);
  else usarComoReferencia(sg.valor);
}

/* CEP: tenta o Google primeiro; sem ele, usa o ViaCEP; sem internet, avisa. */
function resolverCep(cep){
  estado.busca.carregando = true;
  pintarSugestoes();
  const formatado = formatarCep(cep);

  if(estado.google.pronto && google.maps.Geocoder){
    new google.maps.Geocoder().geocode({
      componentRestrictions:{postalCode: formatado, country:"BR"}
    }, (res, status) => {
      estado.busca.carregando = false;
      if(status === "OK" && res && res[0]){
        const loc = res[0].geometry.location;
        aplicarLocal(loc.lat(), loc.lng(), res[0].formatted_address.split(" - ")[0]);
        avisar("CEP encontrado", formatado + " · " + estado.rascunho.bairro, "ok");
      } else {
        buscarViaCep(cep, formatado);
      }
    });
    return;
  }
  buscarViaCep(cep, formatado);
}

function buscarViaCep(cep, formatado){
  fetch("https://viacep.com.br/ws/" + soDigitos(cep) + "/json/")
    .then(r => r.json())
    .then(d => {
      estado.busca.carregando = false;
      if(d.erro) throw new Error("CEP inexistente");
      const rotulo = [d.logradouro, d.bairro].filter(Boolean).join(" — ") || d.localidade;
      const b = BAIRROS.find(b => d.bairro &&
        b.nome.toLowerCase() === d.bairro.toLowerCase());
      if(b){
        aplicarLocal(b.lat + (Math.random()*.006 - .003),
                     b.lng + (Math.random()*.006 - .003), rotulo);
        avisar("CEP encontrado", formatado + " · " + b.nome, "ok");
      } else {
        usarComoReferencia(rotulo);
        avisar("CEP encontrado", "Marque o ponto no mapa para precisar o local.", "alerta");
      }
    })
    .catch(() => {
      estado.busca.carregando = false;
      pintarSugestoes();
      avisar("CEP não encontrado", "Confira o número ou marque o ponto no mapa.", "alerta");
    });
}

function resolverLugar(placeId, rotulo){
  if(!estado.google.pronto) return;
  new google.maps.Geocoder().geocode({placeId}, (res, status) => {
    if(status === "OK" && res && res[0]){
      const loc = res[0].geometry.location;
      aplicarLocal(loc.lat(), loc.lng(), rotulo || res[0].formatted_address.split(" - ")[0]);
    } else {
      avisar("Endereço não localizado", "Marque o ponto tocando no mapa.", "alerta");
    }
  });
}

function irParaBairro(nome){
  const b = BAIRROS.find(b => b.nome === nome);
  if(!b) return;
  const texto = estado.busca.texto.trim();
  aplicarLocal(b.lat, b.lng, texto && texto.toLowerCase() !== nome.toLowerCase()
    ? texto + " — " + nome : nome);
}

function usarComoReferencia(texto){
  const r = estado.rascunho;
  if(!r) return;
  r.referencia = texto;
  r.refEditada = true;
  atualizarRotuloLocal();
  avisar("Referência anotada", "Agora toque no mapa para marcar o ponto.", "alerta");
}

/* Aplica uma coordenada vinda da busca, seja qual for a origem. */
function aplicarLocal(lat, lng, rotulo){
  const r = estado.rascunho;
  if(!r) return;
  const b = bairroPorCoordenada(lat, lng);
  r.lat = lat;
  r.lng = lng;
  r.bairro = b.nome;
  if(rotulo){ r.referencia = rotulo; r.refEditada = true; }
  estado.busca.texto = rotulo || estado.busca.texto;

  if(estado.google.pronto && estado.google.mapaApp){
    estado.google.mapaApp.panTo({lat, lng});
    estado.google.mapaApp.setZoom(17);
    marcarLocalRascunho(lat, lng, estado.google.mapaApp);
    const campo = document.getElementById("campoBusca");
    if(campo) campo.value = estado.busca.texto;
  } else {
    r.x = b.x + (Math.random()*16 - 8);
    r.y = b.y + (Math.random()*16 - 8);
    desenhar();
  }
}

function limparBusca(){
  estado.busca.texto = "";
  estado.busca.sugestoes = [];
  estado.busca.carregando = false;
  const campo = document.getElementById("campoBusca");
  if(campo){ campo.value = ""; campo.focus(); }
  pintarSugestoes();
}

function teclaBusca(evt){
  if(evt.key === "Enter"){
    evt.preventDefault();
    if(estado.busca.sugestoes.length) escolherSugestao(0);
    else if(ehCep(estado.busca.texto)) resolverCep(estado.busca.texto);
  }
  if(evt.key === "Escape"){ estado.busca.sugestoes = []; pintarSugestoes(); }
}

/* =====================================================================
   LOCALIZAÇÃO DO USUÁRIO
   Usa a API de Geolocalização do navegador. Ela exige HTTPS ou localhost
   e permissão explícita da pessoa — por isso todo o fluxo continua
   funcionando por busca e por toque no mapa se a detecção falhar.
   ===================================================================== */

/* Distância em metros entre duas coordenadas (fórmula de Haversine). */
function distanciaMetros(lat1, lng1, lat2, lng2){
  const R = 6371000, rad = Math.PI / 180;
  const dLat = (lat2 - lat1) * rad;
  const dLng = (lng2 - lng1) * rad;
  const a = Math.sin(dLat/2)**2 +
            Math.cos(lat1*rad) * Math.cos(lat2*rad) * Math.sin(dLng/2)**2;
  return 2 * R * Math.asin(Math.sqrt(a));
}

function fmtDistancia(m){
  if(m < 1000) return Math.round(m / 10) * 10 + " m";
  return (m / 1000).toFixed(1).replace(".", ",") + " km";
}

function detectarLocalizacao(marcarNoMapa){
  const L = estado.local;
  L.jaPerguntou = true;

  if(!navigator.geolocation){
    definirFalhaLocal("Este navegador não oferece geolocalização.");
    return;
  }
  if(window.isSecureContext === false){
    definirFalhaLocal("O navegador só libera o GPS em HTTPS ou localhost. "
      + "Abra o arquivo por um servidor local.");
    return;
  }

  L.status = "buscando";
  pintarCartaoLocal();

  navigator.geolocation.getCurrentPosition(
    posicao => {
      L.status = "ok";
      L.lat = posicao.coords.latitude;
      L.lng = posicao.coords.longitude;
      L.precisao = Math.round(posicao.coords.accuracy);
      L.mensagem = "";

      const b = bairroPorCoordenada(L.lat, L.lng);
      const longe = distanciaMetros(L.lat, L.lng, CENTRO_RECIFE.lat, CENTRO_RECIFE.lng) > 60000;

      if(marcarNoMapa && estado.rascunho){
        aplicarLocal(L.lat, L.lng, null);
      }
      pintarCartaoLocal();
      if(estado.telaApp === "mapa") desenhar();

      avisar("Localização encontrada",
        (longe ? "Você parece estar fora do Recife. " : "") +
        b.nome + " · precisão de cerca de " + L.precisao + " m", longe ? "alerta" : "ok");
    },
    erro => {
      if(erro.code === 1){
        definirFalhaLocal("Permissão negada. Você pode liberar no cadeado da barra de endereço.");
      } else if(erro.code === 2){
        definirFalhaLocal("O aparelho não conseguiu obter a posição agora.");
      } else {
        definirFalhaLocal("A busca demorou demais. Tente de novo ou marque no mapa.");
      }
    },
    {enableHighAccuracy:true, timeout:12000, maximumAge:120000}
  );
}

function definirFalhaLocal(mensagem){
  estado.local.status = "erro";
  estado.local.mensagem = mensagem;
  pintarCartaoLocal();
  avisar("Não deu para te localizar", mensagem, "alerta");
}

/* Cartão que aparece no passo 3, redesenhado sem recarregar a tela toda. */
function cartaoLocalizacao(){
  const L = estado.local;
  if(L.status === "buscando"){
    return `<div class="cartao-local">
      <span class="selo-ico pulso-mira">${ICO.alvo}</span>
      <span class="texto"><b>Procurando você…</b>
        <span>Confirme a permissão de localização no aviso do navegador.</span></span>
    </div>`;
  }
  if(L.status === "ok"){
    const b = bairroPorCoordenada(L.lat, L.lng);
    return `<div class="cartao-local ativo">
      <span class="selo-ico">${ICO.alvo}</span>
      <span class="texto"><b>Você está em ${b.nome}</b>
        <span>Precisão de cerca de ${L.precisao} m · ajuste no mapa se precisar</span></span>
      <button class="acao" onclick="detectarLocalizacao(true)">Atualizar</button>
    </div>`;
  }
  if(L.status === "erro"){
    return `<div class="cartao-local erro">
      <span class="selo-ico">${ICO.aviso}</span>
      <span class="texto"><b>Localização indisponível</b>
        <span>${L.mensagem}</span></span>
      <button class="acao" onclick="detectarLocalizacao(true)">Tentar</button>
    </div>`;
  }
  return `<div class="cartao-local">
    <span class="selo-ico">${ICO.alvo}</span>
    <span class="texto"><b>Estou no local agora</b>
      <span>Deixe o app pegar sua posição pelo GPS</span></span>
    <button class="acao forte" onclick="detectarLocalizacao(true)">Usar GPS</button>
  </div>`;
}

function pintarCartaoLocal(){
  const alvo = document.getElementById("cartaoLocal");
  if(alvo) alvo.innerHTML = cartaoLocalizacao();
}

/* Ordena os chamados pela distância real até a pessoa, quando ela é conhecida. */
function chamadosPertoDeMim(quantos){
  const abertos = estado.chamados.filter(c => c.situacao !== "RESOLVIDO");
  const L = estado.local;
  if(L.status !== "ok"){
    return {lista: abertos.sort((a,b) => prioridade(b) - prioridade(a)).slice(0, quantos),
            porDistancia: false};
  }
  const comDistancia = abertos.map(c => {
    c.distancia = distanciaMetros(L.lat, L.lng, c.lat, c.lng);
    return c;
  }).sort((a,b) => a.distancia - b.distancia);
  return {lista: comDistancia.slice(0, quantos), porDistancia: true};
}

function passoLocal(){
  const r = estado.rascunho;
  const temLocal = !!(r.lat || r.x);
  return `<div class="passo surge">
    <span class="eyebrow">PASSO 3 DE 4</span>
    <h3>Onde fica?</h3>
    <p class="apoio">O app tenta te localizar sozinho. Se preferir, busque o endereço ou toque no mapa.</p>

    <div id="cartaoLocal">${cartaoLocalizacao()}</div>

    <div class="busca-caixa">
      <div class="busca-campo">
        ${ICO.lupa}
        <input id="campoBusca" type="search" autocomplete="off" inputmode="text"
               placeholder="Endereço ou CEP (ex: 50050-900)"
               value="${(estado.busca.texto || "").replace(/"/g, "&quot;")}"
               oninput="aoDigitarBusca(this.value)" onkeydown="teclaBusca(event)">
        <span class="loader-agua pequeno" id="rodaBusca" style="display:none"><i></i><i></i><i></i></span>
        ${estado.busca.texto ? `<button class="limpar" onclick="limparBusca()" aria-label="Limpar busca">×</button>` : ""}
      </div>
      <div id="listaSugestoes"></div>
    </div>
    <p class="dica-busca">${ICO.carta} Digite os 8 números do CEP para o app achar a rua sozinho.</p>

    <div style="border-radius:var(--r);overflow:hidden;border:1px solid var(--linha);margin-bottom:12px">
      ${blocoMapa({idSvg:"mapaAppSvg", idGoogle:"mapaAppGoogle", clicavel:true,
                   instrucao:"Toque no mapa para ajustar o ponto"})}
    </div>
    <div id="resumoLocal">${r.bairro
      ? `<div class="quadro"><div class="linha-dado"><span>Bairro</span><span>${r.bairro}</span></div></div>`
      : ""}</div>
    <input class="campo" id="campoRef" placeholder="Complemento (nº, esquina, ponto de referência)"
           value="${(r.referencia || "").replace(/"/g, "&quot;")}" oninput="editarReferencia(this.value)">
    <div class="acoes">
      <button class="btn btn-2" onclick="voltarPasso()">Voltar</button>
      <button class="btn btn-1" id="btnLocal" ${temLocal ? "" : "disabled"}
              onclick="avancarPasso()">Continuar</button>
    </div>
  </div>`;
}

function passoConfirmar(){
  const r = estado.rascunho;
  const t = TIPOS.find(t => t.id === r.tipo);
  const p = PORTES.find(p => p.id === r.porte);
  const vazao = t.fixa ? t.fixa : Math.round(p.lh * t.mult);
  const porDia = vazao * 24;
  return `<div class="passo surge">
    <span class="eyebrow">PASSO 4 DE 4</span>
    <h3>Confira e envie</h3>
    <p class="apoio">${r.referencia || "Sem ponto de referência"}</p>

    ${r.autoIA ? `<p class="ident-nota">Tipo e tamanho sugeridos pela foto (confiança ${Math.round(r.autoIA.confianca * 100)}%) e confirmados por você.</p>` : ""}
    <div class="quadro">
      <div class="linha-dado"><span>Tipo</span><span>${t.nome}</span></div>
      <div class="linha-dado"><span>Porte</span><span>${t.fixa ? "estimado por consumo" : p.nome}</span></div>
      <div class="linha-dado"><span>Bairro</span><span>${r.bairro || "—"}</span></div>
      <div class="linha-dado"><span>Prazo do tipo</span><span>${t.metaH} h</span></div>
    </div>
    <div class="quadro destaque">
      <div class="linha-dado"><span>Perda estimada</span><span>${fmt(vazao)} L/h</span></div>
      <div class="linha-dado"><span>Em 24 horas</span><span>${fmtLitros(porDia)}</span></div>
      <div class="linha-dado"><span>Equivale a</span>
        <span>${familias(porDia).toFixed(1).replace(".",",")} famílias/mês</span></div>
    </div>

    <div class="bloco-foto" style="margin:4px 0 16px">
      ${r.foto ? `<div class="foto-previa">
          <img src="${r.foto}" alt="Prévia da foto do chamado">
          <button class="foto-remover" onclick="removerFotoRascunho()" aria-label="Remover foto">×</button>
        </div>
        <label class="btn btn-2 foto-trocar">Trocar foto
          <input type="file" accept="image/*" onchange="selecionarFotoRascunho(this)" hidden></label>`
      : `<label class="foto-adicionar">
          <span class="foto-icone">${ICO.camera}</span>
          <span class="texto"><b>Adicionar foto</b><span>Tire uma foto ou escolha da galeria — ajuda a equipe a achar e entender o problema (opcional)</span></span>
          <input type="file" accept="image/*" capture="environment" onchange="selecionarFotoRascunho(this)" hidden>
        </label>`}
      ${estado.erroFoto ? `<p class="erro-foto" role="alert">${estado.erroFoto}</p>` : ""}
    </div>

    <label for="campoDescricao" class="rotulo-campo">Quer contar mais alguma coisa? (opcional)</label>
    <textarea id="campoDescricao" class="campo campo-texto" rows="3"
      placeholder="Ex: piora à noite, tem cheiro forte, alaga a calçada toda…"
      oninput="editarDescricao(this.value)">${escaparHtml(r.descricao || "")}</textarea>

    <div class="acoes">
      <button class="btn btn-2" onclick="voltarPasso()">Voltar</button>
      <button class="btn btn-1" onclick="enviarChamado()">Enviar chamado</button>
    </div>
  </div>`;
}

function telaRecibo(){
  const c = estado.ultimo;
  const fila = filaOrdenada().filter(x => x.situacao !== "RESOLVIDO");
  const pos = fila.findIndex(x => x.protocolo === c.protocolo) + 1;
  return `<div class="recibo surge">
    <div class="copo"><span class="agua"></span>${ICO.check}</div>
    <h3>Chamado aberto</h3>
    <div class="protocolo">${c.protocolo}</div>
    <div style="display:flex;justify-content:center;margin-bottom:12px">
      ${blocoFotoChamado(c, "grande")}
    </div>
    <div class="quadro">
      <div class="linha-dado"><span>Perda estimada</span>
        <span style="color:var(--obra)">${fmt(c.vazao)} L/h</span></div>
      <div class="linha-dado"><span>Prazo de atendimento</span><span>${c.metaH} h</span></div>
      <div class="linha-dado"><span>Posição na fila</span><span>${pos}º de ${fila.length}</span></div>
    </div>
    <p style="font-size:12.5px;color:var(--cinza);margin:14px 0 0">
      ${pos === 1
        ? "Seu chamado é o mais urgente da fila agora."
        : "A fila é por volume perdido, não por ordem de chegada: "
          + (pos - 1 === 1 ? "há 1 ponto perdendo" : "há " + (pos - 1) + " pontos perdendo")
          + " mais água neste momento."}
    </p>
    <button class="btn btn-2" style="width:100%;margin-top:14px;display:flex;align-items:center;
              justify-content:center;gap:7px" onclick="compartilharProtocolo('${c.protocolo}')">
      ${ICO.compartilhar}Compartilhar
    </button>
    <div class="acoes">
      <button class="btn btn-2" onclick="navegarApp('perfil')">Meus chamados</button>
      <button class="btn btn-1" onclick="trocarVista('op')">Ver no painel</button>
    </div>
  </div>`;
}

/* ---------- ações do fluxo ---------- */
function escolherTipo(id){ estado.rascunho.tipo = id; estado.passo = 2; desenhar(); }
function escolherPorte(id){
  estado.rascunho.porte = id;
  estado.passo = 3;
  desenhar();
  // No passo do local, quem está na rua normalmente está diante do vazamento:
  // vale tentar o GPS uma vez, sem insistir se a pessoa recusar.
  if(!estado.local.jaPerguntou){
    detectarLocalizacao(true);
  } else if(estado.local.status === "ok" && estado.rascunho && !estado.rascunho.lat){
    aplicarLocal(estado.local.lat, estado.local.lng, null);
  }
}
/* Escapa HTML antes de injetar texto livre digitado pela pessoa —
   boa prática mesmo num protótipo local, sem back-end nem outros usuários. */
function escaparHtml(texto){
  const div = document.createElement("div");
  div.textContent = texto;
  return div.innerHTML;
}

function editarReferencia(v){ estado.rascunho.referencia = v; estado.rascunho.refEditada = true; }
/* Lê a foto, reduz para no máx. 900 px e guarda como JPEG (data URL) —
   fica leve e não estoura a memória do protótipo. */
function selecionarFotoRascunho(input){
  const arq = input.files && input.files[0];
  if(!arq) return;
  estado.erroFoto = "";
  if(!arq.type.startsWith("image/")){ estado.erroFoto = "Esse arquivo não é uma imagem."; desenhar(); return; }
  const leitor = new FileReader();
  leitor.onerror = () => { estado.erroFoto = "Não consegui ler a foto. Tente outra."; desenhar(); };
  leitor.onload = () => {
    const img = new Image();
    img.onerror = () => { estado.erroFoto = "Não consegui abrir essa imagem."; desenhar(); };
    img.onload = () => {
      const k = Math.min(1, 900 / Math.max(img.width, img.height));
      const c = document.createElement("canvas");
      c.width = Math.round(img.width * k); c.height = Math.round(img.height * k);
      c.getContext("2d").drawImage(img, 0, 0, c.width, c.height);
      if(estado.rascunho) estado.rascunho.foto = c.toDataURL("image/jpeg", .82);
      desenhar();
    };
    img.src = leitor.result;
  };
  leitor.readAsDataURL(arq);
}
function removerFotoRascunho(){ if(estado.rascunho) estado.rascunho.foto = null; desenhar(); }

function editarDescricao(v){ estado.rascunho.descricao = v; }
/* =====================================================================
   CHAMADO REPETIDO + "EU TAMBÉM VI"
   Ao marcar o local, o app procura chamados abertos do mesmo tipo muito
   perto. Se achar, sugere confirmar o que já existe (reforça a prioridade)
   em vez de abrir um duplicado. Também dá para confirmar direto no mapa.
   ===================================================================== */
const RAIO_DUPLICATA_M = 100;      // com GPS/Google Maps
const RAIO_DUPLICATA_PX = 16;      // no mapa esquemático (sem internet)

function posicaoRascunho(){
  const r = estado.rascunho;
  if(r.lat != null) return {lat:r.lat, lng:r.lng};
  if(r.x != null){ const A = ajusteSvg(); return {lng:(r.x - A.x.a) / A.x.b, lat:(r.y - A.y.a) / A.y.b}; }
  return null;
}
function procurarDuplicatas(){
  const r = estado.rascunho, p = posicaoRascunho();
  if(!p) return [];
  return estado.chamados
    .filter(c => c.tipo === r.tipo && c.situacao !== "RESOLVIDO")
    .map(c => {
      const dist = r.lat != null
        ? distanciaMetros(r.lat, r.lng, c.lat, c.lng)
        : (c.x != null ? Math.hypot(r.x - c.x, r.y - c.y) * (RAIO_DUPLICATA_M / RAIO_DUPLICATA_PX) : Infinity);
      return {c, dist};
    })
    .filter(x => x.dist <= RAIO_DUPLICATA_M)
    .sort((a, b) => a.dist - b.dist).slice(0, 3);
}
function jaApoiou(c){ return !!c.apoiadoPorMim; }
function podeApoiar(c){ return !c.meu && c.situacao !== "RESOLVIDO" && !jaApoiou(c); }
function textoApoios(c){
  const n = c.apoiosTotal || 0;
  return n ? `${n} ${n === 1 ? "pessoa confirmou" : "pessoas confirmaram"}` : "";
}

function avancarPasso(){
  if(estado.passo === 3 && estado.rascunho && !estado.rascunho.ignorouDuplicata){
    const dups = procurarDuplicatas();
    if(dups.length){ estado.duplicata = dups; desenhar(); return; }
  }
  estado.passo++; desenhar();
}
function voltarPasso(){
  if(estado.duplicata){ estado.duplicata = null; desenhar(); return; }
  estado.passo = Math.max(1, estado.passo - 1); desenhar();
}
function seguirComoNovo(){
  estado.rascunho.ignorouDuplicata = true;
  estado.duplicata = null; estado.passo = 4; desenhar();
}

async function apoiarChamado(protocolo, vindoDoRegistro){
  const c = estado.chamados.find(x => x.protocolo === protocolo);
  if(!c || !podeApoiar(c)) return;
  if(c.simulado){
    c.apoiosTotal++; c.apoiadoPorMim = true;
  } else {
    try{ Object.assign(c, chamadoDaApi(await API.apoiar(c.id))); }
    catch(e){ avisar("Não foi possível confirmar", e.message, "alerta"); return; }
  }
  const n = c.apoiosTotal;
  avisar("Obrigado por confirmar", c.referencia + " · " + n + (n === 1 ? " pessoa já viu" : " pessoas já viram")
    + ". A prioridade subiu.", "ok");
  if(vindoDoRegistro){
    if(estado.google.pinoRascunho){ estado.google.pinoRascunho.setMap(null); estado.google.pinoRascunho = null; }
    estado.rascunho = null; estado.duplicata = null; estado.ident = null; estado.passo = 1;
    estado.busca = {texto:"", sugestoes:[], carregando:false};
    estado.selecionado = protocolo;
    navegarApp("mapa");
    return;
  }
  if(estado.google.pronto){
    pintarPinosGoogle(estado.google.mapaApp, "app"); pintarPinosGoogle(estado.google.mapaOp, "op");
  }
  if(estado.detalheAberto === protocolo) renderizarDetalheChamado();
  if(estado.vista === "op") pintarPainel(false);
  else if(estado.telaApp === "mapa"){
    const folha = document.getElementById("folhaMapa"); if(folha) folha.innerHTML = folhaChamado();
  } else desenhar();
}

function passoDuplicata(){
  const itens = estado.duplicata.map(({c, dist}) => {
    const t = TIPOS.find(x => x.id === c.tipo), meu = c.meu;
    const apoios = textoApoios(c);
    return `<div class="cartao com-foto dup-cartao">
      ${blocoFotoChamado(c, "mini")}
      <div class="cartao-corpo">
        <b>${t.nome}</b>
        <div class="meta">${c.referencia}</div>
        <div class="meta">a ~${Math.round(dist)} m do ponto que você marcou · aberto há ${fmtHoras(horasEmAberto(c))}${apoios ? " · " + apoios : ""}</div>
        ${meu
          ? `<button class="btn btn-2 dup-botao" onclick="cancelar();navegarApp('perfil')">É o que eu já registrei</button>`
          : jaApoiou(c)
          ? `<span class="selo resolvido" style="margin-top:8px;display:inline-block">Você já confirmou</span>`
          : `<button class="btn btn-1 dup-botao" onclick="apoiarChamado('${c.protocolo}', true)">É o mesmo — eu também vi</button>`}
      </div>
    </div>`;
  }).join("");
  return `<div class="passo surge">
    <span class="eyebrow">PASSO 3 DE 4 · ANTES DE ENVIAR</span>
    <h3>Já existe um chamado parecido aqui</h3>
    <p class="apoio">Se for o mesmo vazamento, confirme o que já existe: a prioridade dele sobe e a equipe não recebe registro repetido.</p>
    <div class="escada">${itens}</div>
    <div class="acoes">
      <button class="btn btn-2" onclick="voltarPasso()">Voltar</button>
      <button class="btn btn-2" onclick="seguirComoNovo()">É outro vazamento</button>
    </div>
  </div>`;
}

function cancelar(){
  estado.ident = null;
  estado.duplicata = null;
  estado.rascunho = null;
  estado.passo = 1;
  estado.busca = {texto:"", sugestoes:[], carregando:false};
  navegarApp("mapa");
}

async function enviarChamado(){
  const r = estado.rascunho;
  if(!r || estado.enviando) return;
  estado.enviando = true;
  let novo;
  try{
    const local = completarLocal({lat:r.lat, lng:r.lng, x:r.x, y:r.y});
    const foto = r.foto ? await (await fetch(r.foto)).blob() : null;     // a foto já foi reduzida para JPEG
    novo = chamadoDaApi(await API.registrar({
      tipo:r.tipo, porte:r.porte, descricao:(r.descricao || "").trim(),
      endereco:(r.referencia || "").slice(0, 255), latitude:local.lat, longitude:local.lng}, foto));
  }catch(e){
    avisar("Não foi possível enviar o chamado", e.message, "alerta");
    return;
  }finally{
    estado.enviando = false;
  }
  estado.chamados.push(novo);
  estado.ultimo = novo;
  estado.selecionado = novo.protocolo;
  estado.rascunho = null;
  estado.ident = null;
  estado.duplicata = null;
  estado.busca = {texto:"", sugestoes:[], carregando:false};
  if(estado.google.pinoRascunho){ estado.google.pinoRascunho.setMap(null); estado.google.pinoRascunho = null; }
  estado.passo = 5;
  avisar("Chamado aberto", novo.protocolo + " · " + fmt(novo.vazao) + " L/h estimados", "ok");
  desenhar();
  setTimeout(() => zapChamadoRegistrado(novo), 1200);
}

/* =====================================================================
   PAINEL DE OPERAÇÃO
   ===================================================================== */
function filaOrdenada(){
  return [...estado.chamados].sort((a,b) => {
    if(a.situacao === "RESOLVIDO" && b.situacao !== "RESOLVIDO") return 1;
    if(b.situacao === "RESOLVIDO" && a.situacao !== "RESOLVIDO") return -1;
    return prioridade(b) - prioridade(a);
  });
}

function pintarFila(){
  const alvo = document.getElementById("filaOp");
  if(!alvo) return;
  const completa = filaOrdenada();
  const maxPrio = Math.max(...estado.chamados.map(prioridade), 1);

  // Mantém a posição verdadeira (a prioridade real do sistema) mesmo
  // quando a lista está filtrada — filtrar não deve inventar um ranking novo.
  const rankVerdadeiro = {};
  completa.forEach((c, i) => { rankVerdadeiro[c.protocolo] = i + 1; });

  const filtrados = completa.filter(passaFiltroFila);

  const contagem = document.getElementById("contagemFila");
  if(contagem){
    contagem.textContent = filtrosAtivos()
      ? `${filtrados.length} de ${completa.length} chamados`
      : "ordenada por perda";
  }

  if(!filtrados.length){
    alvo.innerHTML = `<p style="color:var(--cinza);font-size:13px;text-align:center;padding:26px 8px">
      Nenhum chamado com esse filtro.<br>
      <button class="filtro-limpar" style="margin-top:10px" onclick="limparFiltrosFila()">Limpar filtros</button>
    </p>`;
    return;
  }

  alvo.innerHTML = filtrados.map(c => {
    const t = TIPOS.find(t => t.id === c.tipo);
    const cor = CORES[nivel(c)];
    const acoes = c.situacao === "ABERTO"
      ? `<button class="mini forte" onclick="event.stopPropagation();despachar('${c.protocolo}')">${ICO.caminhao}Despachar</button>`
      : c.situacao === "REPARO"
      ? `<button class="mini forte" onclick="event.stopPropagation();concluir('${c.protocolo}')">${ICO.check}Concluir</button>`
      : `<span class="selo resolvido">Reparado em ${fmtHoras(c.horasAteReparo)}</span>`;
    return `<div class="item-fila${estado.selecionado === c.protocolo ? " ativo" : ""}${c.protocolo === estado.recemChegado ? " novo" : ""}"
              id="item-${c.protocolo}" style="border-left-color:${cor}">
      ${blocoFotoChamado(c, "mini")}
      <div class="item-fila-corpo">
        <div class="cabeca">
          <span class="pos">${c.situacao === "RESOLVIDO" ? "—" : rankVerdadeiro[c.protocolo] + "º"} · ${c.protocolo}</span>
          <span class="perda">${fmt(c.vazao)} L/h</span>
          ${c.protocolo === estado.recemChegado ? '<span class="selo aberto">novo</span>' : ""}
        </div>
        <div class="linha-titulo">
          <button class="titulo-chamado" onclick="selecionarChamado('${c.protocolo}')">${t.nome}</button>
          <button class="botao-detalhe" onclick="event.stopPropagation();abrirDetalheChamado('${c.protocolo}')"
                  aria-label="Ver detalhes completos de ${c.protocolo}">${ICO.info}</button>
        </div>
        <div class="onde">${c.referencia}</div>
        ${c.descricao ? `<p class="relato">${escaparHtml(c.descricao)}”</p>` : ""}
        <div class="barra"><i style="width:${Math.min(100, prioridade(c)/maxPrio*100)}%;background:${cor}"></i></div>
        <div class="rodape">
          <span class="onde">${fmtHoras(horasEmAberto(c))} · ${fmtLitros(volumePerdido(c))}${(c.apoiosTotal || 0) ? " · ✓ " + c.apoiosTotal : ""}
            ${atraso(c) > 0 && c.situacao !== "RESOLVIDO"
              ? '· <b style="color:var(--alerta)">fora do prazo</b>' : ""}</span>
          ${acoes}
        </div>
      </div>
    </div>`;
  }).join("");
}

function pintarPainel(animar){
  const abertos = estado.chamados.filter(c => c.situacao !== "RESOLVIDO");
  const vazaoTotal = abertos.reduce((s,c) => s + c.vazao, 0);
  const volumeTotal = abertos.reduce((s,c) => s + volumePerdido(c), 0);
  const resolvidos = estado.chamados.filter(c => c.situacao === "RESOLVIDO");
  const estancado = resolvidos.reduce((s,c) => s + c.vazao, 0);
  const atrasados = abertos.filter(c => atraso(c) > 0).length;

  if(animar){
    contar("kpiVazao", vazaoTotal, fmt);
    contar("kpiVolume", volumeTotal, fmtLitros);
    contar("kpiAbertos", abertos.length, n => String(Math.round(n)));
    contar("kpiEstancado", estancado, fmt);
  } else {
    texto("kpiVazao", fmt(vazaoTotal));
    texto("kpiVolume", fmtLitros(volumeTotal));
    texto("kpiAbertos", String(abertos.length));
    texto("kpiEstancado", fmt(estancado));
  }
  texto("kpiFamilias", "= consumo mensal de " + fmt(familias(volumeTotal)) + " famílias");
  texto("kpiAtrasados", atrasados ? atrasados + " fora do prazo do tipo" : "todos dentro do prazo");
  const satisfacao = calcularSatisfacaoMedia();
  texto("kpiResolvidos", resolvidos.length
    ? resolvidos.length + " reparo(s) concluído(s)"
      + (satisfacao ? " · satisfação " + satisfacao.media.toFixed(1).replace(".",",") + "/5" : "")
    : "nenhum reparo concluído ainda");

  const caixa = document.getElementById("caixaMapaOp");
  if(caixa && (!estado.google.pronto || !document.getElementById("mapaOpGoogle"))){
    caixa.innerHTML = blocoMapa({idSvg:"mapaOpSvg", idGoogle:"mapaOpGoogle", clicavel:false});
    ativarMapas();
  } else if(estado.google.pronto){
    pintarPinosGoogle(estado.google.mapaOp, "op");
  }

  pintarFila();
  pintarRankingOp();
  pintarColaboradores();
  pintarAvaliacoesOp();
  pintarGraficoEvolucao();
  pintarAgoraOp(abertos, atrasados);
}

/* Resumo em linguagem simples do que está acontecendo no painel neste momento. */
function pintarAgoraOp(abertos, atrasados){
  const el = document.getElementById("agoraOp");
  if(!el) return;
  if(!abertos.length){
    el.innerHTML = "<b>Agora:</b> nenhum chamado aberto. A rede está em dia.";
    return;
  }
  const emReparo = abertos.filter(c => c.situacao === "REPARO").length;
  const porPrio = abertos.slice().sort((a,b) => prioridade(b) - prioridade(a));
  const topo = porPrio[0];
  el.innerHTML = "<b>Agora:</b> " + abertos.length + " chamado(s) aberto(s), "
    + (atrasados ? atrasados + " fora do prazo" : "todos dentro do prazo")
    + (emReparo ? ", " + emReparo + " com equipe em reparo" : "")
    + ". O primeiro da fila é <b>" + escaparHtml(topo.referencia) + "</b>, perdendo "
    + fmt(topo.vazao) + " L/h.";
}

/* ---------- ranking do painel: por bairro ou por tipo de ocorrência ---------- */
function trocarModoRanking(modo){
  estado.rankingModoOp = modo;
  document.getElementById("botaoRankingBairro").setAttribute("aria-pressed", modo === "bairro");
  document.getElementById("botaoRankingTipo").setAttribute("aria-pressed", modo === "tipo");
  document.getElementById("rotuloRanking").innerHTML = modo === "bairro"
    ? `${ICO.bairro}Perda por bairro`
    : `${ICO.grafico}Perda por tipo`;
  pintarRankingOp();
}

function pintarRankingOp(){
  const abertos = estado.chamados.filter(c => c.situacao !== "RESOLVIDO");
  const agrupado = {};

  if(estado.rankingModoOp === "tipo"){
    for(const c of abertos){
      const nome = TIPOS.find(t => t.id === c.tipo).nome;
      agrupado[nome] = (agrupado[nome] || 0) + c.vazao;
    }
  } else {
    for(const c of abertos) agrupado[c.bairro] = (agrupado[c.bairro] || 0) + c.vazao;
  }

  const ordenado = Object.entries(agrupado).sort((a,b) => b[1] - a[1]).slice(0,6);
  const topo = ordenado.length ? ordenado[0][1] : 1;
  const totalGeral = ordenado.reduce((s,[,v]) => s + v, 0) || 1;
  document.getElementById("rankingOp").innerHTML = ordenado.length
    ? ordenado.map(([nome,v]) => `<div class="barra-bairro">
        <div class="l"><span>${nome}</span><span>${fmt(v)} L/h · ${Math.round(v/totalGeral*100)}%</span></div>
        <div class="t"><i style="width:${v/topo*100}%"></i></div></div>`).join("")
    : `<p style="color:var(--cinza);font-size:13px;margin:0">Nenhum chamado aberto.</p>`;
}

/* Muda a situação de um chamado. Os reais passam pela API; os simulados mudam só aqui. */
async function mudarSituacao(protocolo, nova){
  const c = estado.chamados.find(c => c.protocolo === protocolo);
  if(!c) return null;
  if(c.simulado){
    if(nova === "RESOLVIDO") c.horasAteReparo = horasEmAberto(c);
    c.situacao = nova;
    if(nova === "REPARO") c.despachadoEm = Date.now(); else c.concluidoEm = Date.now();
    return c;
  }
  try{
    const antes = c.avaliacao;
    Object.assign(c, chamadoDaApi(await API.atualizarSituacao(c.id, nova)));
    if(antes && c.avaliacao) c.avaliacao.contatoRealizado = antes.contatoRealizado;
    return c;
  }catch(e){
    avisar("Não foi possível atualizar", e.message, "alerta");
    return null;
  }
}

function repintarDepoisDeMudar(protocolo){
  pintarPainel(false);
  if(estado.google.pronto) pintarPinosGoogle(estado.google.mapaOp, "op");
  if(estado.detalheAberto === protocolo) renderizarDetalheChamado();
}

async function despachar(protocolo){
  const c = await mudarSituacao(protocolo, "REPARO");
  if(!c) return;
  avisar("Equipe despachada", c.referencia, "alerta");
  if(c.meu) zapChamadoDespachado(c);
  repintarDepoisDeMudar(protocolo);
}

async function concluir(protocolo){
  const c = await mudarSituacao(protocolo, "RESOLVIDO");
  if(!c) return;
  avisar("Reparo concluído", fmt(c.vazao) + " L/h pararam de escorrer em " + c.bairro, "ok");
  if(c.meu) zapChamadoConcluido(c);
  repintarDepoisDeMudar(protocolo);
}

/* =====================================================================
   LINHA DO TEMPO DO CHAMADO
   Mostra os passos já cumpridos (com hora) e os que ainda faltam,
   deixando claro que o sistema acompanha o problema até o fim —
   não só recebe o registro e some.
   ===================================================================== */
function fmtHorario(timestamp){
  return new Date(timestamp).toLocaleString("pt-BR",
    {day:"2-digit", month:"2-digit", hour:"2-digit", minute:"2-digit"});
}

function blocoTimeline(c){
  const passos = [
    {rotulo:"Registrado", feito:true, quando:c.abertoEm},
    {rotulo:"Equipe despachada", feito:!!c.despachadoEm, quando:c.despachadoEm},
    {rotulo:"Reparo concluído", feito:!!c.concluidoEm, quando:c.concluidoEm},
    {rotulo:"Você avaliou", feito:!!c.avaliacao, quando:c.avaliacao ? c.avaliacao.quando : null}
  ];
  const html = passos.map(p => `<li class="passo-tempo${p.feito ? " feito" : ""}">
      <span class="ponto-tempo" aria-hidden="true"></span>
      <span class="rotulo-tempo">${p.rotulo}</span>
      <span class="hora-tempo">${p.feito ? fmtHorario(p.quando) : "Aguardando"}</span>
    </li>`).join("");
  return `<details class="timeline-chamado">
    <summary>Ver linha do tempo</summary>
    <ol class="lista-tempo">${html}</ol>
  </details>`;
}

/* =====================================================================
   DETALHE EXPANDIDO DO CHAMADO (painel de operação)
   Reúne numa tela só o que hoje fica espalhado pela lista: foto grande,
   relato do cidadão, linha do tempo completa e as ações de atendimento.
   ===================================================================== */
let elementoAntesDoModal = null;

function abrirDetalheChamado(protocolo){
  const c = estado.chamados.find(c => c.protocolo === protocolo);
  if(!c) return;
  estado.detalheAberto = protocolo;
  elementoAntesDoModal = document.activeElement;
  renderizarDetalheChamado();
  const modal = document.getElementById("modalDetalhe");
  modal.classList.remove("oculto");
  const botaoFechar = modal.querySelector(".modal-fechar");
  if(botaoFechar) botaoFechar.focus();
}

function fecharDetalheChamado(){
  estado.detalheAberto = null;
  document.getElementById("modalDetalhe").classList.add("oculto");
  if(elementoAntesDoModal && elementoAntesDoModal.focus) elementoAntesDoModal.focus();
}

document.addEventListener("keydown", evt => {
  if(evt.key === "Escape" && estado.detalheAberto) fecharDetalheChamado();
});

function renderizarDetalheChamado(){
  const alvo = document.getElementById("modalDetalheConteudo");
  if(!alvo || !estado.detalheAberto) return;
  const c = estado.chamados.find(c => c.protocolo === estado.detalheAberto);
  if(!c){ fecharDetalheChamado(); return; }

  const t = TIPOS.find(t => t.id === c.tipo);
  const situ = {ABERTO:"aberto", REPARO:"reparo", RESOLVIDO:"resolvido"}[c.situacao];
  const situTxt = {ABERTO:"Na fila", REPARO:"Equipe em campo", RESOLVIDO:"Reparo concluído"}[c.situacao];

  const acoes = c.situacao === "ABERTO"
    ? `<button class="btn btn-1" onclick="despachar('${c.protocolo}')">${ICO.caminhao}Despachar equipe</button>`
    : c.situacao === "REPARO"
    ? `<button class="btn btn-1" onclick="concluir('${c.protocolo}')">${ICO.check}Concluir reparo</button>`
    : "";

  alvo.innerHTML = `
    <h3 id="modalDetalheTitulo" class="modal-titulo">${t.nome}</h3>
    <p class="modal-sub">${c.protocolo} · ${c.bairro} · <span class="selo ${situ}">${situTxt}</span></p>
    ${blocoFotoChamado(c, "grande")}
    <div class="quadro">
      <div class="linha-dado"><span>Endereço</span><span>${c.referencia}</span></div>
      <div class="linha-dado"><span>Autor do registro</span><span>${c.autor}</span></div>
      <div class="linha-dado"><span>Prazo do tipo</span><span>${c.metaH} h</span></div>
      <div class="linha-dado"><span>Tempo em aberto</span><span>${fmtHoras(horasEmAberto(c))}</span></div>
      <div class="linha-dado"><span>Confirmações</span><span>${(c.apoiosTotal || 0) ? textoApoios(c) : "nenhuma ainda"}</span></div>
    </div>
    <div class="quadro destaque">
      <div class="linha-dado"><span>Vazão estimada</span><span>${fmt(c.vazao)} L/h</span></div>
      <div class="linha-dado"><span>Volume perdido</span><span>${fmtLitros(volumePerdido(c))}</span></div>
      <div class="linha-dado"><span>Prioridade</span><span>${nivel(c).toUpperCase()}</span></div>
    </div>
    ${c.descricao ? `<p class="relato">${escaparHtml(c.descricao)}”</p>` : ""}
    ${blocoTimeline(c)}
    ${c.avaliacao ? `<div class="avaliacao avaliado">
        <span class="estrelas-fixas" aria-hidden="true">${"★".repeat(c.avaliacao.nota)}${"☆".repeat(5 - c.avaliacao.nota)}</span>
        <span class="avaliacao-texto">Avaliação de ${escaparHtml(c.avaliacao.por || c.autor)}</span>
        ${c.avaliacao.comentario ? `<p class="relato">${escaparHtml(c.avaliacao.comentario)}”</p>` : ""}
        ${blocoContatoBaixaAvaliacao(c)}
      </div>` : ""}
    ${estado.vista === "app" && podeApoiar(c) ? `<div class="acoes" style="margin-top:16px"><button class="btn btn-1" onclick="apoiarChamado('${c.protocolo}')">Eu também vi</button></div>` : ""}
    ${acoes ? `<div class="acoes" style="margin-top:16px">${acoes}</div>` : ""}
  `;
}


/* =====================================================================
   AVALIAÇÃO PÓS-REPARO
   Fecha o ciclo: depois que a equipe conclui, quem registrou o chamado
   confirma se o reparo resolveu de verdade. Isso vira um indicador de
   qualidade — não basta fechar o chamado, o cidadão precisa concordar.
   ===================================================================== */
/* Passo 1: escolher a nota — abre o campo de comentário, ainda não grava. */
/* =====================================================================
   COMPARTILHAR PROTOCOLO
   Usa a Web Share API nativa quando o navegador suporta (abre o menu de
   compartilhamento do próprio aparelho); sem suporte, cai para um link
   do WhatsApp Web com o texto pronto — as duas são ações reais, não
   simuladas, mas quem decide o que fazer com o link é sempre a pessoa.
   ===================================================================== */
function compartilharProtocolo(protocolo){
  const c = estado.chamados.find(c => c.protocolo === protocolo);
  if(!c) return;
  const t = TIPOS.find(t => t.id === c.tipo);
  const texto = "💧 Registrei um vazamento no Água em Dia\n"
    + t.nome + " em " + c.bairro + "\n"
    + "Protocolo " + c.protocolo + " · " + fmt(c.vazao) + " L/h estimados\n"
    + "Prazo de atendimento: " + c.metaH + "h";

  if(navigator.share){
    navigator.share({title:"Água em Dia", text:texto}).catch(() => {});
    return;
  }
  const url = "https://wa.me/?text=" + encodeURIComponent(texto);
  window.open(url, "_blank", "noopener");
  avisar("Compartilhamento aberto", "Escolha o contato no WhatsApp Web", "ok");
}

function iniciarAvaliacao(protocolo, nota){
  estado.avaliacaoRascunho = {protocolo, nota, comentario:""};
  desenhar();
}

function editarComentarioAvaliacao(v){
  if(estado.avaliacaoRascunho) estado.avaliacaoRascunho.comentario = v;
}

function trocarNotaRascunho(nota){
  if(estado.avaliacaoRascunho) estado.avaliacaoRascunho.nota = nota;
  desenhar();
}

function cancelarAvaliacao(){
  estado.avaliacaoRascunho = null;
  desenhar();
}

/* Passo 2: confirma — grava nota + comentário (o comentário é opcional). */
async function confirmarAvaliacao(protocolo){
  const r = estado.avaliacaoRascunho;
  if(!r || r.protocolo !== protocolo) return;
  const c = estado.chamados.find(c => c.protocolo === protocolo);
  if(!c || c.situacao !== "RESOLVIDO") return;
  const comentario = (r.comentario || "").trim();
  if(c.simulado){
    c.avaliacao = {nota:r.nota, comentario, quando:Date.now(), contatoRealizado:false};
  } else {
    try{ Object.assign(c, chamadoDaApi(await API.avaliar(c.id, r.nota, comentario || null))); }
    catch(e){ avisar("Não foi possível enviar a avaliação", e.message, "alerta"); return; }
  }
  estado.avaliacaoRascunho = null;
  zapEnviar("⭐ *Avaliação recebida: " + r.nota + " de 5.*\nObrigado! Sua opinião ajuda a melhorar o atendimento.");
  avisar("Obrigado pela avaliação", r.nota + " de 5" +
    (c.avaliacao.comentario ? " · com comentário" : "") + " · " + c.referencia, "ok");
  desenhar();
  if(estado.vista === "op") pintarPainel(false);
}

/** Estrelas clicáveis → comentário opcional → confirmar. Fixas se já avaliado. */
function blocoAvaliacao(c){
  if(c.situacao !== "RESOLVIDO"){
    return "";
  }

  if(c.avaliacao){
    const cheias = "★".repeat(c.avaliacao.nota) + "☆".repeat(5 - c.avaliacao.nota);
    return `<div class="avaliacao avaliado" aria-label="Você avaliou este reparo com ${c.avaliacao.nota} de 5 estrelas">
      <span class="estrelas-fixas" aria-hidden="true">${cheias}</span>
      <span class="avaliacao-texto">Você avaliou este reparo</span>
      ${c.avaliacao.comentario ? `<p class="relato">${escaparHtml(c.avaliacao.comentario)}”</p>` : ""}
    </div>`;
  }

  const rascunho = estado.avaliacaoRascunho;
  if(rascunho && rascunho.protocolo === c.protocolo){
    const estrelasEdicao = [5,4,3,2,1].map(n =>
      `<button class="estrela${n <= rascunho.nota ? " marcada" : ""}"
         aria-label="Avaliar reparo com ${n} de 5 estrelas" aria-pressed="${n === rascunho.nota}"
         onclick="event.stopPropagation();trocarNotaRascunho(${n})">★</button>`
    ).join("");
    return `<div class="avaliacao avaliacao-em-curso" role="group" aria-label="Concluir avaliação">
      <span class="avaliacao-texto">Sua nota: ${rascunho.nota} de 5</span>
      <span class="estrelas-interativas">${estrelasEdicao}</span>
      <label for="comentario-${c.protocolo}" class="rotulo-campo" style="margin-top:8px">
        Quer contar o que faltou? (opcional)</label>
      <textarea id="comentario-${c.protocolo}" class="campo campo-texto" rows="2"
        style="margin-bottom:8px"
        placeholder="Ex: demorou, mas o serviço ficou bem-feito"
        onclick="event.stopPropagation()"
        oninput="editarComentarioAvaliacao(this.value)">${escaparHtml(rascunho.comentario)}</textarea>
      <div class="acoes" style="margin-top:0">
        <button class="btn btn-2" onclick="event.stopPropagation();cancelarAvaliacao()">Cancelar</button>
        <button class="btn btn-1" onclick="event.stopPropagation();confirmarAvaliacao('${c.protocolo}')">Enviar avaliação</button>
      </div>
    </div>`;
  }

  const estrelas = [5,4,3,2,1].map(n =>
    `<button class="estrela" aria-label="Avaliar reparo com ${n} de 5 estrelas"
       onclick="event.stopPropagation();iniciarAvaliacao('${c.protocolo}', ${n})">★</button>`
  ).join("");
  return `<div class="avaliacao" role="group" aria-label="Avaliar este reparo">
    <span class="avaliacao-texto">O reparo resolveu? Avalie:</span>
    <span class="estrelas-interativas">${estrelas}</span>
  </div>`;
}

/** Média das avaliações de todos os reparos concluídos, para o painel. */
/* =====================================================================
   CONTATO COM O CIDADÃO EM AVALIAÇÕES BAIXAS
   Quando a nota é 1 ou 2, a operação vê uma sugestão de contato. É uma
   ação SIMULADA de propósito — o protótipo não liga nem manda mensagem
   de verdade, só registra que o contato foi feito, porque gerar um
   número de telefone falso e discar de verdade correria o risco de
   ligar para uma pessoa real por coincidência.
   ===================================================================== */
function blocoContatoBaixaAvaliacao(c){
  if(!c.avaliacao || c.avaliacao.nota > 2) return "";
  if(c.avaliacao.contatoRealizado){
    return `<div class="alerta-contato">
      <button class="mini contatado" disabled>${ICO.check}Contato já registrado</button>
    </div>`;
  }
  return `<div class="alerta-contato">
    <span class="alerta-contato-texto">${ICO.aviso}
      Nota baixa — vale entrar em contato com ${c.autor} para entender o que faltou.</span>
    <button class="mini forte" onclick="event.stopPropagation();contatarCidadao('${c.protocolo}')">
      ${ICO.telefone}Entrar em contato</button>
    <div class="alerta-contato-nota">Ação simulada — o protótipo não liga nem envia mensagem de verdade.</div>
  </div>`;
}

function contatarCidadao(protocolo){
  const c = estado.chamados.find(c => c.protocolo === protocolo);
  if(!c || !c.avaliacao || c.avaliacao.contatoRealizado) return;
  c.avaliacao.contatoRealizado = true;
  avisar("Contato registrado", "A equipe vai falar com " + c.autor + " sobre o chamado "
    + c.protocolo + " (ação simulada — protótipo)", "alerta");
  pintarAvaliacoesOp();
  if(estado.detalheAberto === protocolo) renderizarDetalheChamado();
}

/* =====================================================================
   AVALIAÇÕES ALEATÓRIAS
   A cada intervalo, um chamado resolvido e ainda não avaliado (de outro
   cidadão, não do usuário logado — a dele é sempre feita manualmente pela pessoa
   testando o app) recebe uma nota simulada. Mantém o painel de
   avaliações e o indicador de satisfação vivos durante a apresentação.
   ===================================================================== */
const INTERVALO_AVALIACAO_MS = 75000;   // um pouco mais espaçado que os novos chamados

const AVALIADORES = ["Renan","Thales","Pedro","Marina","Carlos","Juliana"];
const COMENTARIOS_COMUNS = {
  bons: ["Resolveram rápido, muito obrigado!", "Equipe educada e eficiente. Nota máxima.",
         "Vazamento sumiu e a rua ficou limpa. Parabéns!", "Atendimento excelente, voltou tudo ao normal.",
         "Chegaram no horário combinado e resolveram de vez."],
  neutros: ["Resolveu, mas demorou mais do que o prazo.", "Ficou ok, só faltou avisar quando a equipe chegou.",
            "Serviço feito, mas a calçada ficou meio bagunçada."],
  ruins: ["Voltou a vazar poucos dias depois.", "Ninguém avisou nada durante o atendimento.",
          "Demorou demais e o problema continua em parte."]
};
/* Distribuição realista: maioria dos reparos agrada, uma fatia menor decepciona. */
const NOTAS_PESADAS = [5,5,5,4,4,4,3,3,2,1];

function gerarAvaliacaoAleatoria(){
  const candidatos = estado.chamados.filter(c =>
    c.situacao === "RESOLVIDO" && !c.avaliacao && !c.meu);
  if(!candidatos.length) return null;

  const c = candidatos[Math.floor(Math.random() * candidatos.length)];
  const nota = NOTAS_PESADAS[Math.floor(Math.random() * NOTAS_PESADAS.length)];

  const outros = AVALIADORES.filter(n => n !== c.autor);
  const avaliador = outros[Math.floor(Math.random() * outros.length)];
  let comentario = "";
  {
    const faixa = nota >= 4 ? "bons" : nota === 3 ? "neutros" : "ruins";
    const lista = COMENTARIOS_COMUNS[faixa];
    comentario = lista[Math.floor(Math.random() * lista.length)];
  }

  c.avaliacao = {nota, comentario, quando: Date.now(), contatoRealizado: false, por: avaliador};
  return c;
}

function simularAvaliacaoAleatoria(){
  const c = gerarAvaliacaoAleatoria();
  if(!c) return;
  const critico = c.avaliacao.nota <= 2;
  avisar(critico ? "Avaliação crítica recebida" : "Nova avaliação recebida",
    c.avaliacao.por + " avaliou com " + c.avaliacao.nota + " de 5" + (critico ? " ⚠" : ""),
    critico ? "alerta" : "ok");
  if(estado.vista === "op") pintarPainel(false);
}

function calcularSatisfacaoMedia(){
  const avaliados = estado.chamados.filter(c => c.avaliacao);
  if(!avaliados.length) return null;
  const soma = avaliados.reduce((s,c) => s + c.avaliacao.nota, 0);
  return {media: soma / avaliados.length, total: avaliados.length};
}

/* =====================================================================
   INFRAESTRUTURA
   ===================================================================== */
function texto(id, valor){
  const el = document.getElementById(id);
  if(el) el.textContent = valor;
}

/* contagem animada dos indicadores */
function contar(id, alvo, formatar){
  const el = document.getElementById(id);
  if(!el) return;
  if(window.matchMedia && window.matchMedia("(prefers-reduced-motion: reduce)").matches){
    el.textContent = formatar(alvo);
    return;
  }
  const inicio = performance.now(), dur = 900;
  function quadro(agora){
    const t = Math.min(1, (agora - inicio) / dur);
    const suave = 1 - Math.pow(1 - t, 3);
    el.textContent = formatar(alvo * suave);
    if(t < 1) requestAnimationFrame(quadro);
  }
  requestAnimationFrame(quadro);
}

function avisar(titulo, texto, tipo){
  const caixa = document.getElementById("avisos");
  const el = document.createElement("div");
  el.className = "aviso " + (tipo || "");
  el.setAttribute("role", "status");
  el.innerHTML = "<b>" + titulo + "</b>" + texto;
  caixa.appendChild(el);
  setTimeout(() => {
    el.style.transition = "opacity .3s, transform .3s";
    el.style.opacity = "0";
    el.style.transform = "translateX(12px)";
    setTimeout(() => el.remove(), 320);
  }, 3600);
}

function moverIndicador(){
  const ind = document.getElementById("indicadorAba");
  const ativo = document.getElementById(estado.vista === "app" ? "aba-app" : "aba-op");
  if(!ind || !ativo) return;
  ind.style.width = ativo.offsetWidth + "px";
  ind.style.transform = "translateX(" + (ativo.offsetLeft - 4) + "px)";
}

/* o indicador acompanha o tamanho real dos botões (fonte carrega depois, tema muda a fonte) */
(function(){
  const refazer = () => requestAnimationFrame(moverIndicador);
  if(document.fonts && document.fonts.ready) document.fonts.ready.then(refazer);
  addEventListener("resize", refazer); addEventListener("load", refazer);
  if("ResizeObserver" in window){
    const ro = new ResizeObserver(refazer);
    ["aba-app","aba-op"].forEach(id => { const e = document.getElementById(id); if(e) ro.observe(e); });
  }
})();

function trocarVista(v){
  estado.vista = v;
  document.getElementById("vista-app").classList.toggle("oculto", v !== "app");
  document.getElementById("vista-op").classList.toggle("oculto", v !== "op");
  document.getElementById("aba-app").setAttribute("aria-selected", v === "app");
  document.getElementById("aba-op").setAttribute("aria-selected", v === "op");
  moverIndicador();
  if(v === "op"){
    const caixa = document.getElementById("caixaMapaOp");
    if(caixa) caixa.innerHTML = "";
    estado.google.pinos.op = {};
    pintarPainel(true);
  } else {
    desenhar();
  }
}

function desenhar(){
  if(estado.vista === "app"){
    document.getElementById("foneCorpo").innerHTML = telaApp();
    pintarNav();
    ativarMapas();
  } else {
    pintarPainel(false);
  }
}

/* contador do topo */
function tique(){
  const vazaoTotal = estado.chamados
    .filter(c => c.situacao !== "RESOLVIDO")
    .reduce((s,c) => s + c.vazao, 0);
  estado.litrosNaTela += vazaoTotal / 3600;
  texto("medidorTopo", fmt(estado.litrosNaTela));
  if(estado.vista === "op"){
    const abertos = estado.chamados.filter(c => c.situacao !== "RESOLVIDO");
    const volumeTotal = abertos.reduce((s,c) => s + volumePerdido(c), 0);
    texto("kpiVolume", fmtLitros(volumeTotal));
    texto("kpiFamilias", "= consumo mensal de " + fmt(familias(volumeTotal)) + " famílias");
  }
}

/* Efeito de "respingo" ao tocar nos botões principais — puramente
   decorativo, reforça o tema de água no toque. Usa delegação de evento
   porque os botões são recriados a cada redesenho (innerHTML). */
function ativarRespingos(seletor){
  document.addEventListener("pointerdown", evt => {
    const alvo = evt.target.closest(seletor);
    if(!alvo) return;
    const r = alvo.getBoundingClientRect();
    const tamanho = Math.max(r.width, r.height) * 1.2;
    const bolha = document.createElement("span");
    bolha.className = "respingo-agua";
    bolha.style.width = bolha.style.height = tamanho + "px";
    bolha.style.left = (evt.clientX - r.left - tamanho / 2) + "px";
    bolha.style.top = (evt.clientY - r.top - tamanho / 2) + "px";
    alvo.appendChild(bolha);
    setTimeout(() => bolha.remove(), 600);
  });
}


/* =====================================================================
   ENTRADA: INTRO ANIMADA, LOGIN E CADASTRO
   Protótipo: as contas ficam só no localStorage DESTE navegador. A senha
   é guardada como hash (SHA-256) e o CPF como hash + versão mascarada —
   nunca em texto puro. Num sistema de verdade isso vive num servidor.
   ===================================================================== */
function lsGet(k){ try{ return localStorage.getItem(k); }catch(e){ return null; } }
function lsSet(k,v){ try{ localStorage.setItem(k,v); }catch(e){} }
function lsDel(k){ try{ localStorage.removeItem(k); }catch(e){} }
/* =====================================================================
   FOTOS DE PERFIL
   - Usuários fictícios: coloque as imagens na pasta "fotos/" ao lado deste
     HTML, com os nomes abaixo (ou troque os caminhos). Sem arquivo, aparecem
     as iniciais. Dá para usar também uma URL ou um data URL.
   - Usuário logado: troca a própria foto pelo Perfil; fica salva neste navegador.
   ===================================================================== */
const FOTOS_PERFIL = {
  "Água em Dia":"data:image/svg+xml;utf8,%3Csvg%20xmlns=%22http://www.w3.org/2000/svg%22%20viewBox=%220%200%20200%20200%22%3E%3Cdefs%3E%3ClinearGradient%20id=%22f%22%20x1=%220%22%20y1=%220%22%20x2=%221%22%20y2=%221%22%3E%3Cstop%20offset=%220%22%20stop-color=%22%234DB4FA%22/%3E%3Cstop%20offset=%22.6%22%20stop-color=%22%231B6CC4%22/%3E%3Cstop%20offset=%221%22%20stop-color=%22%23082B66%22/%3E%3C/linearGradient%3E%3ClinearGradient%20id=%22p%22%20x1=%220%22%20y1=%220%22%20x2=%220%22%20y2=%221%22%3E%3Cstop%20offset=%220%22%20stop-color=%22%23FFFFFF%22/%3E%3Cstop%20offset=%221%22%20stop-color=%22%23CBE9FF%22/%3E%3C/linearGradient%3E%3ClinearGradient%20id=%22g%22%20x1=%22.2%22%20y1=%220%22%20x2=%22.8%22%20y2=%221%22%3E%3Cstop%20offset=%220%22%20stop-color=%22%23FFFFFF%22/%3E%3Cstop%20offset=%221%22%20stop-color=%22%23A9DAFB%22/%3E%3C/linearGradient%3E%3CradialGradient%20id=%22r%22%20cx=%22.3%22%20cy=%22.1%22%20r=%22.9%22%3E%3Cstop%20offset=%220%22%20stop-color=%22%23fff%22%20stop-opacity=%22.28%22/%3E%3Cstop%20offset=%221%22%20stop-color=%22%23fff%22%20stop-opacity=%220%22/%3E%3C/radialGradient%3E%3Cfilter%20id=%22b%22%20x=%22-30%25%22%20y=%22-30%25%22%20width=%22160%25%22%20height=%22160%25%22%3E%3CfeGaussianBlur%20stdDeviation=%229%22/%3E%3C/filter%3E%3C/defs%3E%3Crect%20width=%22200%22%20height=%22200%22%20fill=%22url%28%23f%29%22/%3E%3Crect%20width=%22200%22%20height=%22200%22%20fill=%22url%28%23r%29%22/%3E%3Cg%20transform=%22translate%2836%2012%29%20scale%28.72%29%22%3E%3Cpath%20d=%22M100%20231C61%20180%2022%20141%2022%20101a78%2078%200%201%201%20156%200c0%2040-39%2079-78%20130Z%22%20transform=%22translate%280%2012%29%22%20fill=%22%2302194A%22%20opacity=%22.5%22%20filter=%22url%28%23b%29%22/%3E%3Cpath%20d=%22M100%20231C61%20180%2022%20141%2022%20101a78%2078%200%201%201%20156%200c0%2040-39%2079-78%20130Z%22%20fill=%22none%22%20stroke=%22url%28%23p%29%22%20stroke-width=%2215%22%20stroke-linejoin=%22round%22/%3E%3Cpath%20d=%22M100%2050C100%2050%2056%20106%2056%20131a44%2044%200%200%200%2088%200C144%20106%20100%2050%20100%2050Z%22%20fill=%22url%28%23g%29%22/%3E%3Cpath%20d=%22M80%20140a26%2026%200%200%200%2024%2027%22%20fill=%22none%22%20stroke=%22%232A8BE0%22%20stroke-width=%228%22%20stroke-linecap=%22round%22/%3E%3Cpath%20d=%22M82%20112C86%20102%2092%2094%2098%2086%22%20fill=%22none%22%20stroke=%22%23fff%22%20stroke-width=%226%22%20stroke-linecap=%22round%22%20opacity=%22.9%22/%3E%3C/g%3E%3C/svg%3E",
  "Pedro":"data:image/jpeg;base64,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",
};
function chaveFotoUsuario(){ return "aed_foto_" + (USUARIO.email || USUARIO.nome); }
function fotoDe(nome){
  if(nome === USUARIO.nome) return USUARIO.foto || "";
  return FOTOS_PERFIL[nome] || "";
}
/* Miolo de um avatar: as iniciais ficam por baixo e a foto (se carregar) cobre. */
function miolioAvatar(nome, iniciais){
  const f = fotoDe(nome);
  return escaparHtml(iniciais) + (f
    ? `<img class="avatar-foto" src="${escaparHtml(f)}" alt="" onerror="this.remove()">` : "");
}
function atualizarAvatarTopo(){
  const a = document.getElementById("avatarTopo");
  if(a) a.innerHTML = miolioAvatar(USUARIO.nome, iniciaisNomeSimples(USUARIO.nome));
}
function iniciaisNomeSimples(nome){
  const p = String(nome).trim().split(/\s+/);
  return ((p[0] || "?")[0] + (p.length > 1 ? p[p.length-1][0] : "")).toUpperCase();
}
function trocarFotoPerfil(input){
  const arq = input.files && input.files[0];
  input.value = "";
  if(!arq) return;
  if(!arq.type.startsWith("image/")){ avisar("Foto de perfil", "Escolha uma imagem.", "alerta"); return; }
  const leitor = new FileReader();
  leitor.onerror = () => avisar("Foto de perfil", "Não consegui ler a imagem.", "alerta");
  leitor.onload = () => {
    const img = new Image();
    img.onerror = () => avisar("Foto de perfil", "Não consegui abrir essa imagem.", "alerta");
    img.onload = () => {
      const lado = Math.min(img.width, img.height), T = 256;
      const c = document.createElement("canvas"); c.width = c.height = T;
      c.getContext("2d").drawImage(img, (img.width - lado)/2, (img.height - lado)/2, lado, lado, 0, 0, T, T);
      USUARIO.foto = c.toDataURL("image/jpeg", .85);
      lsSet(chaveFotoUsuario(), USUARIO.foto);
      atualizarAvatarTopo(); desenhar();
      avisar("Foto de perfil", "Foto atualizada.", "ok");
    };
    img.src = leitor.result;
  };
  leitor.readAsDataURL(arq);
}
function removerFotoPerfil(){
  USUARIO.foto = ""; lsDel(chaveFotoUsuario());
  atualizarAvatarTopo(); desenhar();
}

const USUARIO = {nome:"Visitante", usuario:"", email:"", tel:"", cpfMasc:"", foto:""};
let GOOGLE_CLIENT_ID = "";   // vem do servidor (GOOGLE_CLIENT_ID); vazio = login com Google desligado

function mascararCPF(v){
  v = String(v).replace(/\D/g,"").slice(0,11);
  return v.replace(/(\d{3})(\d)/,"$1.$2").replace(/(\d{3})(\d)/,"$1.$2").replace(/(\d{3})(\d{1,2})$/,"$1-$2");
}
function mascararTel(v){
  v = String(v).replace(/\D/g,"").slice(0,11);
  if(v.length <= 2) return v ? "(" + v : "";
  if(v.length <= 6) return "(" + v.slice(0,2) + ") " + v.slice(2);
  if(v.length <= 10) return "(" + v.slice(0,2) + ") " + v.slice(2,6) + "-" + v.slice(6);
  return "(" + v.slice(0,2) + ") " + v.slice(2,7) + "-" + v.slice(7);
}
function cpfValido(c){
  c = String(c).replace(/\D/g,"");
  if(c.length !== 11 || /^(\d)\1{10}$/.test(c)) return false;
  for(let t = 9; t < 11; t++){
    let s = 0;
    for(let i = 0; i < t; i++) s += Number(c[i]) * (t + 1 - i);
    const d = ((s * 10) % 11) % 10;
    if(d !== Number(c[t])) return false;
  }
  return true;
}
function limparUsuario(v){ return String(v).toLowerCase().replace(/^@+/,"").replace(/[^a-z0-9._]/g,"").slice(0,20); }
function emailValido(e){ return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(e); }
function telValido(v){ const d = String(v).replace(/\D/g,""); return d.length >= 10 && d.length <= 11 && Number(d.slice(0,2)) >= 11; }

function iniciaisUsuario(){
  const p = USUARIO.nome.trim().split(/\s+/);
  return escaparHtml(((p[0] || "?")[0] + (p.length > 1 ? p[p.length-1][0] : "")).toUpperCase());
}
/* ----- animação de abertura ----- */
let introTimer = null;
function iniciarIntro(){
  const reduzido = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
  document.getElementById("telaIntro").classList.add("toca");
  introTimer = setTimeout(pularIntro, reduzido ? 800 : 4300);
}
function pularIntro(){
  clearTimeout(introTimer);
  const intro = document.getElementById("telaIntro");
  if(intro.classList.contains("oculto")) return;
  intro.classList.add("saindo");
  setTimeout(async () => {
    intro.classList.add("oculto");
    if(API.temToken()){
      try{ return entrarNoApp(contaDaApi(await API.eu())); }
      catch(e){ /* sessão vencida: segue para o login */ }
    }
    document.getElementById("telaAuth").classList.remove("oculto");
    iniciarGoogleReal();
    const campo = document.getElementById("entEmail"); if(campo) campo.focus({preventScroll:true});
  }, 450);
}

/* ----- abas e utilidades de formulário ----- */
function authAba(qual){
  const entrar = qual === "entrar";
  document.getElementById("tabEntrar").setAttribute("aria-selected", String(entrar));
  document.getElementById("tabCriar").setAttribute("aria-selected", String(!entrar));
  document.getElementById("formEntrar").classList.toggle("oculto", !entrar);
  document.getElementById("formCriar").classList.toggle("oculto", entrar);
  document.getElementById("authSub").textContent = entrar
    ? "Entre para registrar e acompanhar vazamentos na sua rua."
    : "Crie sua conta em menos de um minuto.";
}
function alternarSenha(id, btn){
  const c = document.getElementById(id), ver = c.type === "password";
  c.type = ver ? "text" : "password"; btn.textContent = ver ? "ocultar" : "mostrar";
}
function erroForm(id, msg, campo){
  const el = document.getElementById(id); el.textContent = msg || "";
  if(msg && campo){ const c = document.getElementById(campo); if(c) c.focus(); }
  return !msg;
}
/* ----- recuperar senha: código de 6 dígitos enviado por e-mail ----- */
let _recTimer = null;
function mascararEmail(e){ const [l, d] = String(e).split("@"); return (l.slice(0, 2) + "***") + "@" + (d || ""); }
function nomeDoEmail(e){ return e.split("@")[0].replace(/[._-]+/g, " ").replace(/\b\w/g, m => m.toUpperCase()); }
function esqueciSenha(){
  document.getElementById("authPassoPrincipal").classList.add("oculto");
  ["formCompletar","formCodigo"].forEach(i => document.getElementById(i).classList.add("oculto"));
  document.getElementById("formRecuperar").classList.remove("oculto");
  document.getElementById("authTitulo").textContent = "Recuperar senha";
  document.getElementById("authSub").textContent = "Você recebe um código de 6 dígitos por e-mail.";
  const a = document.getElementById("recAlvo"); a.value = document.getElementById("entEmail").value.trim(); erroForm("erroRecuperar", ""); a.focus();
}
async function enviarRecuperar(ev){
  ev.preventDefault(); erroForm("erroRecuperar", "");
  const email = document.getElementById("recAlvo").value.trim().toLowerCase();
  if(!emailValido(email)) return erroForm("erroRecuperar", "Esse e-mail não parece válido.", "recAlvo"), false;
  estado.recup = {email, nome:nomeDoEmail(email), codigo:null, expira:0, reenvio:0};
  if(!await pedirCodigoDeRecuperacao(false, "erroRecuperar")) return false;
  document.getElementById("formRecuperar").classList.add("oculto");
  document.getElementById("formCodigo").classList.remove("oculto");
  document.getElementById("authTitulo").textContent = "Digite o código";
  document.getElementById("authSub").textContent = "Confira a caixa de entrada do seu e-mail.";
  document.getElementById("recAviso").innerHTML = "Se <b>" + escaparHtml(mascararEmail(email)) + "</b> tiver cadastro, enviamos um código de 6 dígitos. Ele vale por <b>10 minutos</b>."
    + (CONFIG.demo ? " No modo demonstração ele chega no app <b>Gmail</b> do celular: toque no banner, ou em “Abrir o Gmail”." : "");
  document.getElementById("recAbrirGmail").classList.toggle("oculto", !CONFIG.demo);
  ["recCodigo","recSenha"].forEach(i => document.getElementById(i).value = ""); erroForm("erroCodigo", "");
  document.getElementById("recCodigo").focus();
  return false;
}
/* Pede o código ao servidor. No modo demonstração a resposta traz o código para o Gmail simulado do celular. */
async function pedirCodigoDeRecuperacao(reenvio, idErro){
  const r = estado.recup;
  try{
    const resp = await API.pedirCodigo(r.email);
    r.reenvio = Date.now() + 15000; r.expira = Date.now() + 10 * 60 * 1000;
    r.codigo = resp.codigoDemonstracao || null;
    if(r.codigo) gmailCodigoRecuperacao(r, reenvio);
  }catch(e){
    erroForm(idErro, e.message); return false;
  }
  clearInterval(_recTimer);
  const bt = document.getElementById("recReenviar");
  const tique = () => {
    const falta = Math.ceil((r.reenvio - Date.now()) / 1000);
    if(falta > 0){ bt.disabled = true; bt.textContent = "Reenviar código em " + falta + "s"; }
    else { bt.disabled = false; bt.textContent = "Reenviar código"; clearInterval(_recTimer); }
  };
  tique(); _recTimer = setInterval(tique, 500);
  return true;
}
async function reenviarCodigo(){
  const r = estado.recup; if(!r || Date.now() < r.reenvio) return;
  if(await pedirCodigoDeRecuperacao(true, "erroCodigo")){
    erroForm("erroCodigo", "");
    avisar("Novo código enviado", "O código anterior deixou de valer.", "ok");
  }
}
async function enviarCodigo(ev){
  ev.preventDefault(); erroForm("erroCodigo", "");
  const r = estado.recup; if(!r) return voltarAuthPrincipal(), false;
  const cod = document.getElementById("recCodigo").value.replace(/\D/g, ""), nova = document.getElementById("recSenha").value;
  if(cod.length !== 6) return erroForm("erroCodigo", "Digite os 6 dígitos do código.", "recCodigo"), false;
  if(nova.length < 8) return erroForm("erroCodigo", "A nova senha precisa ter pelo menos 8 caracteres.", "recSenha"), false;
  try{ await API.redefinirSenha(r.email, cod, nova); }
  catch(e){ return erroForm("erroCodigo", e.message, "recCodigo"), false; }
  clearInterval(_recTimer);
  if(CONFIG.demo) gmailSenhaAlterada(r);
  const email = r.email; estado.recup = null;
  voltarAuthPrincipal(); authAba("entrar");
  document.getElementById("entEmail").value = email; document.getElementById("entSenha").focus();
  avisar("Senha redefinida", "Entre com a nova senha.", "ok");
  return false;
}

/* ----- e-mail e senha (contas reais, guardadas no servidor) ----- */
/* Converte o cidadão devolvido pela API para o objeto que as telas usam. */
function contaDaApi(u){
  return {nome:u.nome, usuario:u.nomeUsuario || "", email:u.email, tel:mascararTel(u.telefone || ""), cpfMasc:u.cpfMascarado || "—"};
}
async function enviarEntrar(ev){
  ev.preventDefault();
  erroForm("erroEntrar", "");
  const email = document.getElementById("entEmail").value.trim().toLowerCase();
  const senha = document.getElementById("entSenha").value;
  if(!emailValido(email)) return erroForm("erroEntrar", "Informe o e-mail da sua conta.", "entEmail"), false;
  if(!senha) return erroForm("erroEntrar", "Informe sua senha.", "entSenha"), false;
  try{
    const usuario = await API.entrar(email, senha, document.getElementById("entLembrar").checked);
    entrarNoApp(contaDaApi(usuario));
  }catch(e){
    erroForm("erroEntrar", e.message);
  }
  return false;
}
async function enviarCriar(ev){
  ev.preventDefault();
  erroForm("erroCriar", "");
  const campo = id => document.getElementById(id).value.trim();
  const nome = campo("criNome").replace(/\s+/g, " "), usuario = limparUsuario(campo("criUsuario"));
  const cpf = campo("criCpf"), email = campo("criEmail").toLowerCase(), tel = campo("criTel"), senha = document.getElementById("criSenha").value;
  if(nome.length < 3) return erroForm("erroCriar", "Informe seu nome completo.", "criNome"), false;
  if(usuario.length < 3) return erroForm("erroCriar", "O nome de usuário precisa de pelo menos 3 caracteres.", "criUsuario"), false;
  if(!cpfValido(cpf)) return erroForm("erroCriar", "Esse CPF não parece válido.", "criCpf"), false;
  if(!emailValido(email)) return erroForm("erroCriar", "Esse e-mail não parece válido.", "criEmail"), false;
  if(!telValido(tel)) return erroForm("erroCriar", "Informe o celular com DDD.", "criTel"), false;
  if(senha.length < 8) return erroForm("erroCriar", "A senha precisa ter pelo menos 8 caracteres.", "criSenha"), false;
  if(!document.getElementById("criTermos").checked) return erroForm("erroCriar", "É preciso aceitar os termos de uso e a política de privacidade."), false;
  try{
    await API.cadastrar({nome, nomeUsuario:usuario, cpf, email, telefone:tel, senha, aceitouTermos:true});
    const conta = contaDaApi(await API.entrar(email, senha, true));
    entrarNoApp(conta);
    if(CONFIG.demo) gmailBoasVindas(conta);
  }catch(e){
    erroForm("erroCriar", e.message);
  }
  return false;
}

/* ----- Google (só funciona com o ID de cliente configurado no servidor) ----- */
let googleCarregado = false;
function iniciarGoogleReal(){
  if(!GOOGLE_CLIENT_ID || !navigator.onLine || googleCarregado) return;
  googleCarregado = true;
  const s = document.createElement("script");
  s.src = "https://accounts.google.com/gsi/client"; s.async = true;
  s.onload = () => {
    try{
      google.accounts.id.initialize({client_id:GOOGLE_CLIENT_ID, callback:r => entrarComCredencialGoogle(r.credential)});
      google.accounts.id.renderButton(document.getElementById("googleReal"), {theme:"filled_black", size:"large", width:320, text:"continue_with", locale:"pt-BR"});
      document.getElementById("btnGoogle").classList.add("oculto");
    }catch(e){ googleCarregado = false; }
  };
  document.head.appendChild(s);
}
/* Botão "Continuar com Google" quando o servidor ainda não tem o ID de cliente */
function entrarComGoogle(){
  avisar("Login com Google indisponível", "O servidor ainda não tem o ID de cliente do Google configurado.", "alerta");
}
async function entrarComCredencialGoogle(credencial){
  try{
    const r = await API.google(credencial, true);
    if(r.login) return entrarNoApp(contaDaApi(r.login.usuario));
    estado.googlePendente = {credencial, nome:r.nome};
    document.getElementById("authPassoPrincipal").classList.add("oculto");
    document.getElementById("formCompletar").classList.remove("oculto");
    document.getElementById("authTitulo").textContent = "Quase lá";
    document.getElementById("authSub").textContent = "Falta pouco para concluir seu cadastro.";
    document.getElementById("completarQuem").innerHTML = "Olá, <b>" + escaparHtml(r.nome) + "</b>! Informe o CPF e o celular para concluir.";
    document.getElementById("comCpf").focus();
  }catch(e){
    avisar("Google", e.message, "alerta");
  }
}
async function enviarCompletar(ev){
  ev.preventDefault(); erroForm("erroCompletar", "");
  const g = estado.googlePendente; if(!g) return voltarAuthPrincipal(), false;
  const cpf = document.getElementById("comCpf").value.trim(), tel = document.getElementById("comTel").value.trim();
  if(!cpfValido(cpf)) return erroForm("erroCompletar", "Esse CPF não parece válido.", "comCpf"), false;
  if(!telValido(tel)) return erroForm("erroCompletar", "Informe o celular com DDD.", "comTel"), false;
  if(!document.getElementById("comTermos").checked) return erroForm("erroCompletar", "É preciso aceitar os termos de uso e a política de privacidade."), false;
  try{
    const usuario = await API.googleCompletar({credential:g.credencial, cpf, telefone:tel, aceitouTermos:true}, true);
    estado.googlePendente = null;
    const conta = contaDaApi(usuario);
    entrarNoApp(conta);
    if(CONFIG.demo) gmailBoasVindas(conta);
  }catch(e){
    erroForm("erroCompletar", e.message);
  }
  return false;
}
function voltarAuthPrincipal(){
  ["formCompletar","formRecuperar","formCodigo"].forEach(i => document.getElementById(i).classList.add("oculto")); clearInterval(_recTimer);
  document.getElementById("authPassoPrincipal").classList.remove("oculto");
  document.getElementById("authTitulo").textContent = "Bem-vindo(a)";
  authAba(document.getElementById("tabEntrar").getAttribute("aria-selected") === "true" ? "entrar" : "criar");
}

/* ----- sincronização dos chamados com o servidor ----- */
let _syncTimer = null;
async function iniciarSincronizacao(){
  await sincronizarChamados();
  clearInterval(_syncTimer); _syncTimer = setInterval(sincronizarChamados, 10000);
}
function pararSincronizacao(){
  clearInterval(_syncTimer); _syncTimer = null;
  estado.chamados = estado.chamados.filter(c => c.simulado);
}
/* Busca todos os vazamentos e redesenha só se algo mudou (assim vê o que os outros cidadãos registram). */
async function sincronizarChamados(){
  if(!API.temToken()) return;
  let lista;
  try{ lista = await API.listarVazamentos(); }
  catch(e){ return; }                       // sem rede: tenta na próxima rodada (sessão vencida já é tratada pela API)
  const antigos = new Map(estado.chamados.filter(c => !c.simulado).map(c => [c.id, c]));
  const reais = lista.map(v => {
    const novo = chamadoDaApi(v), antigo = antigos.get(v.id);
    if(antigo){
      if(antigo.avaliacao && antigo.avaliacao.contatoRealizado && novo.avaliacao) novo.avaliacao.contatoRealizado = true;
      // situação mudou por outro caminho (ex.: o sistema da empresa): o WhatsApp simulado avisa o cidadão
      if(novo.meu && antigo.situacao !== novo.situacao && CONFIG.demo){
        if(novo.situacao === "REPARO") zapChamadoDespachado(novo); else if(novo.situacao === "RESOLVIDO") zapChamadoConcluido(novo);
      }
    }
    return novo;
  });
  const mudou = JSON.stringify(reais) !== JSON.stringify(estado.chamados.filter(c => !c.simulado));
  estado.chamados = estado.chamados.filter(c => c.simulado).concat(reais);
  if(mudou) redesenharSemInterromper();
}
/* Atualiza a tela visível sem perder o que a pessoa está preenchendo no registro. */
function redesenharSemInterromper(){
  if(estado.vista === "op") pintarPainel(false);
  else if(estado.telaApp !== "registrar") desenhar();
  if(estado.google.pronto){ pintarPinosGoogle(estado.google.mapaApp, "app"); pintarPinosGoogle(estado.google.mapaOp, "op"); }
  if(estado.detalheAberto) renderizarDetalheChamado();
}

/* ----- entrar e sair ----- */
let _fechaEntrada = null;     // temporizador que esconde a tela de entrada depois do login
async function entrarNoApp(conta){
  USUARIO.nome = conta.nome; USUARIO.usuario = conta.usuario || ""; USUARIO.email = conta.email; USUARIO.tel = conta.tel; USUARIO.cpfMasc = conta.cpfMasc;
  document.getElementById("nomeTopo").textContent = conta.nome.split(" ")[0];
  USUARIO.foto = lsGet("aed_foto_" + (conta.email || conta.nome)) || "";
  atualizarAvatarTopo();
  const ent = document.getElementById("entrada");
  ent.classList.add("fechando");
  clearTimeout(_fechaEntrada);
  _fechaEntrada = setTimeout(() => { ent.classList.add("oculto"); ent.classList.remove("fechando"); document.body.classList.remove("travado"); }, 450);
  estado.rascunho = null; estado.passo = 1; estado.telaApp = "mapa";
  await iniciarSincronizacao();
  desenhar();
  if(!estado.local.jaPerguntou) setTimeout(() => detectarLocalizacao(false), 700);
  zapBoasVindas();
  avisar("Bem-vindo(a), " + conta.nome.split(" ")[0], "Login feito. Bom trabalho cuidando da água da cidade.", "ok");
}

/* ----- tema: camisa titular (claro) / terceiro kit 26/27 (escuro) ----- */
/* guarda onde foi o último toque, para a troca de tema "nascer" dali */
let _ultimoToque = {x: innerWidth - 40, y: 40};
addEventListener("pointerdown", e => { _ultimoToque = {x:e.clientX, y:e.clientY}; }, {passive:true, capture:true});

function alternarTema(){
  const raiz = document.documentElement;
  const escuro = raiz.getAttribute("data-tema") !== "escuro";
  const aplicar = () => {
    if(escuro) raiz.setAttribute("data-tema", "escuro"); else raiz.removeAttribute("data-tema");
    lsSet("aed_tema", escuro ? "escuro" : "claro");
  };
  const reduzido = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
  if(!document.startViewTransition || reduzido){ aplicar(); return; }   // sem suporte: troca direta, sem custo
  if(window._trocandoTema) return; window._trocandoTema = true;
  /* vai para o escuro: a tela clara (a "água") baixa e some pelo fundo, como se secasse.
     vai para o claro: a água sobe de baixo e enche a tela. A borda ondula nos dois casos. */
  const classe = escuro ? "vt-seca" : "vt-enche";
  raiz.classList.add("vt-tema", classe);
  const t = document.startViewTransition(aplicar);
  t.ready.then(() => {
    const W = innerWidth, H = innerHeight, amp = Math.max(26, H * .05), N = 30, passos = 56, total = H + 2 * amp;
    const quadros = [];
    for(let k = 0; k <= passos; k++){
      const u = k / passos, e = u < .5 ? 2 * u * u : 1 - Math.pow(-2 * u + 2, 2) / 2;
      const nivel = escuro ? -amp + e * total : H + amp - e * total;
      const pts = [];
      for(let i = 0; i <= N; i++){
        const y = nivel + amp * (.65 * Math.sin(i * .62 + u * 11) + .35 * Math.sin(i * 1.35 - u * 17));
        pts.push((W * i / N).toFixed(1) + "px " + y.toFixed(1) + "px");
      }
      pts.push(W + "px " + (H + amp * 3) + "px", "0px " + (H + amp * 3) + "px");
      quadros.push({clipPath:"polygon(" + pts.join(",") + ")"});
    }
    raiz.animate(quadros, {duration:1300, easing:"linear", fill:"both",
      pseudoElement: escuro ? "::view-transition-old(root)" : "::view-transition-new(root)"});
  }).catch(() => {});
  t.finished.finally(() => { raiz.classList.remove("vt-tema", classe); window._trocandoTema = false; });
}
function sairDaConta(){
  clearTimeout(_fechaEntrada);
  API.sair(); pararSincronizacao();
  USUARIO.nome = "Visitante"; USUARIO.usuario = ""; USUARIO.email = ""; USUARIO.tel = ""; USUARIO.cpfMasc = ""; USUARIO.foto = ""; atualizarAvatarTopo();
  ["entEmail","entSenha","criNome","criUsuario","criCpf","criEmail","criTel","criSenha","comCpf","comTel"].forEach(i => { const e = document.getElementById(i); if(e) e.value = ""; });
  ["criTermos","comTermos"].forEach(i => document.getElementById(i).checked = false);
  const ent = document.getElementById("entrada");
  ent.classList.remove("oculto"); 
  document.getElementById("telaIntro").classList.add("oculto");
  document.getElementById("telaAuth").classList.remove("oculto");
  voltarAuthPrincipal(); authAba("entrar");
  estado.telaApp = "mapa"; estado.zap.msgs = []; estado.zap.naoLidas = 0; atualizarBadgeZap(); zapEsconderBanner(); desenhar();
}


/* =====================================================================
   ROLAGEM: revelar blocos, celular que "chega" e barra de progresso
   ===================================================================== */

/* =====================================================================
   WHATSAPP SIMULADO ("Zap")
   O número cadastrado no login recebe as atualizações dos chamados.
   Nada é enviado de verdade: é só uma demonstração dentro do celular.
   ===================================================================== */
function zapNumero(){
  const t = (USUARIO.tel || "").trim();
  return t && t !== "—" ? t : "(81) 9•••• ••••";
}
function zapHora(ts){ return new Date(ts).toLocaleTimeString("pt-BR", {hour:"2-digit", minute:"2-digit"}); }
function zapFormatar(t){
  return escaparHtml(t).replace(/\*(.+?)\*/g, "<b>$1</b>").replace(/\n/g, "<br>");
}
let zapTimerBanner = null;
function zapEnviar(texto, opcoes){
  if(!CONFIG.demo) return;      // simulação: só no modo demonstração
  opcoes = opcoes || {};
  const m = {texto, quando:Date.now(), lida:false, acao:opcoes.acao || null, rotuloAcao:opcoes.rotuloAcao || ""};
  estado.zap.msgs.push(m);
  if(estado.app === "zap" && estado.zapVista === "chat"){
    m.lida = true; renderZap(); zapRolarFim();
  } else {
    estado.zap.naoLidas++;
    atualizarBadgeZap();
    if(estado.app === "zap") renderZap(); else zapMostrarBanner(m);
  }
  return m;
}
function atualizarBadgeZap(){
  const b = document.getElementById("zapBadgeIcone"); if(!b) return;
  const n = estado.zap.naoLidas; b.hidden = !n; b.textContent = n > 9 ? "9+" : n;
}
function zapRolarFim(){ const c = document.getElementById("zapConversa"); if(c) requestAnimationFrame(() => { c.scrollTop = c.scrollHeight; }); }
/* banner de notificação do celular: fila, um de cada vez */
let _filaBanner = [], _bannerAtivo = false;
function bannerMostrar(b){ _filaBanner.push(b); if(!_bannerAtivo) _bannerProximo(); }
function _bannerProximo(){
  if(_bannerAtivo) return;
  const el = document.getElementById("zapBanner");
  let b = _filaBanner.shift();
  while(b && b.app === estado.app && (b.sempre !== true)) b = _filaBanner.shift();
  if(!el || !b) return;
  _bannerAtivo = true;
  el.innerHTML = `<span class="zb-ico ${b.classeIco || ""}" style="${b.estiloIco || ""}">${b.icone}</span><span class="zb-txt"><span class="zb-topo"><b>${escaparHtml(b.titulo)}</b><i>${escaparHtml(b.canal)} · agora</i></span><span class="zb-msg">${escaparHtml(b.msg)}</span></span>`;
  el.onclick = () => { zapEsconderBanner(); b.ao(); };
  el.classList.remove("some"); el.classList.add("entra");
  clearTimeout(zapTimerBanner);
  zapTimerBanner = setTimeout(zapEsconderBanner, 4600);
}
function zapEsconderBanner(){
  clearTimeout(zapTimerBanner);
  const b = document.getElementById("zapBanner"); if(!b) return;
  b.classList.remove("entra"); b.classList.add("some");
  if(_bannerAtivo){ _bannerAtivo = false; setTimeout(_bannerProximo, 500); }
}
function zapMostrarBanner(m){
  const resumo = m.texto.replace(/\*/g, "").split("\n")[0];
  bannerMostrar({app:"zap", icone:ICO.zap, estiloIco:"", titulo:"Água em Dia", canal:"WhatsApp", msg:resumo,
    ao:() => { estado.zapVista = "chat"; abrirApp("zap"); }});
}
/* ----- o app WhatsApp: lista de conversas e conversa ----- */
function zapAvatar(){ return `<span class="zap-avatar"><img src="${escaparHtml(FOTOS_PERFIL["Água em Dia"])}" alt=""></span>`; }
function renderZap(){
  const el = document.getElementById("appZap"); if(!el) return;
  el.innerHTML = `<div class="barra-status"><span>69:67</span><span>Flamengo · 4L</span></div>` +
    (estado.zapVista === "chat" ? zapChatHtml() : zapListaHtml());
  if(estado.zapVista === "chat") zapRolarFim();
  estado.zapAnimarTodas = false;
}
function zapListaHtml(){
  const msgs = estado.zap.msgs, ult = msgs[msgs.length - 1], n = estado.zap.naoLidas;
  const linha = ult ? `
    <button type="button" class="zl-item" onclick="zapAbrirConversa()">
      ${zapAvatar()}
      <span class="zl-meio"><b>Água em Dia <em aria-label="conta verificada">✓</em></b><small>${escaparHtml(ult.texto.replace(/\*/g,"").split("\n")[0])}</small></span>
      <span class="zl-fim"><i class="${n ? "nao-lida" : ""}">${zapHora(ult.quando)}</i>${n ? `<span class="zl-badge">${n}</span>` : ""}</span>
    </button>` : `<div class="zap-vazio" style="margin:30px 16px">Nenhuma conversa ainda.<br>Entre no app Água em Dia: os avisos dos seus chamados chegam aqui.</div>`;
  return `<div class="zl-topo"><b>WhatsApp</b><span class="zl-ac">${ICO.zap}</span></div>
    <div class="zl-busca">🔍 Pesquisar</div>
    <div class="zl-lista">${linha}</div>
    <p class="zl-rodape">Simulação: nenhuma mensagem real é enviada.</p>`;
}
function zapChatHtml(){
  const msgs = estado.zap.msgs;
  const corpo = msgs.length ? msgs.map((m, i) => `
      <div class="zap-bolha${i === msgs.length - 1 || estado.zapAnimarTodas ? " nova" : ""}" style="--i:${estado.zapAnimarTodas ? i : 0}">
        <p>${zapFormatar(m.texto)}</p>
        ${m.acao ? `<button type="button" class="zap-acao" onclick="${m.acao}">${escaparHtml(m.rotuloAcao || "Abrir")}</button>` : ""}
        <span class="zap-hora">${zapHora(m.quando)}</span>
      </div>`).join("")
    : `<div class="zap-vazio">Nenhuma mensagem ainda.</div>`;
  return `<div class="zap-tela">
    <div class="zap-cab">
      <button type="button" class="zap-volta" onclick="zapVoltarLista()" aria-label="Voltar às conversas">‹</button>
      ${zapAvatar()}
      <span class="zap-quem"><b>Água em Dia <em aria-label="conta verificada">✓</em></b><small>enviando para ${escaparHtml(zapNumero())}</small></span>
    </div>
    <div class="zap-conversa" id="zapConversa">
      <div class="zap-dia">HOJE</div>
      <div class="zap-aviso">Simulação: nenhuma mensagem real é enviada. É assim que os avisos chegariam no WhatsApp.</div>
      ${corpo}
    </div>
    <div class="zap-entrada"><span>Mensagem</span><i>${ICO.zap}</i></div>
  </div>`;
}
function zapAbrirConversa(){
  estado.zapAnimarTodas = true;
  estado.zapVista = "chat"; estado.zap.naoLidas = 0; estado.zap.msgs.forEach(m => m.lida = true);
  atualizarBadgeZap(); renderZap();
}
function zapVoltarLista(){ estado.zapVista = "lista"; renderZap(); }
function zapIrPara(tela){ abrirApp("agua"); navegarApp(tela); }


/* ----- o app Gmail: caixa de entrada e leitura ----- */
const GMAIL_ICO = '<svg viewBox="0 0 48 48" aria-hidden="true"><path fill="#4285F4" d="M6 13.5V35a2 2 0 0 0 2 2h6V20.5z"/><path fill="#34A853" d="M42 13.5V35a2 2 0 0 1-2 2h-6V20.5z"/><path fill="#FBBC04" d="M42 13.5V11a2 2 0 0 0-1-1.7L34 14v6.5z"/><path fill="#EA4335" d="M6 13.5V11a2 2 0 0 1 3.2-1.6L24 20.2 38.8 9.4A2 2 0 0 1 42 11v2.5L24 27z"/><path fill="#C5221F" d="M6 13.5 14 20.5v-6.2L9.2 9.4A2 2 0 0 0 6 11z"/></svg>';
let _gmId = 0;
function gmailNaoLidas(){ return estado.gmail.msgs.filter(m => !m.lida).length; }
function atualizarBadgeGmail(){
  const b = document.getElementById("gmailBadgeIcone"); if(!b) return;
  const n = gmailNaoLidas(); b.hidden = !n; b.textContent = n > 9 ? "9+" : n;
}
function gmailEnviar(m){
  if(!CONFIG.demo) return;      // simulação: só no modo demonstração
  m = Object.assign({id:++_gmId, de:"Água em Dia", deEmail:"nao-responda@aguaemdia.app", quando:Date.now(), lida:false, nova:true}, m);
  estado.gmail.msgs.unshift(m);
  atualizarBadgeGmail();
  if(estado.app === "gmail"){ if(estado.gmailVista === "lista") renderGmail(false); }
  else bannerMostrar({app:"gmail", icone:GMAIL_ICO, classeIco:"gm-banner-ico", titulo:m.de, canal:"Gmail", msg:m.assunto,
    ao:() => { gmailAbrir(m.id); abrirApp("gmail"); gmailAbrir(m.id); }});
  return m;
}
function gmailAbrir(id){
  const m = estado.gmail.msgs.find(x => x.id === id); if(!m) return;
  m.lida = true; m.nova = false; estado.gmailVista = "email"; estado.gmailAberto = id;
  atualizarBadgeGmail(); if(estado.app === "gmail") renderGmail(false);
}
function gmailVoltar(){ estado.gmailVista = "lista"; estado.gmailAberto = null; renderGmail(true); }
function gmailCopiar(cod, bt){
  try{ navigator.clipboard.writeText(cod); }catch(e){}
  if(bt){ bt.textContent = "Código copiado ✓"; setTimeout(() => { bt.textContent = "Copiar código"; }, 1800); }
}
function gmailAvatar(){ return `<span class="zap-avatar"><img src="${escaparHtml(FOTOS_PERFIL["Água em Dia"])}" alt=""></span>`; }
function renderGmail(anima){
  const el = document.getElementById("appGmail"); if(!el) return;
  const aberto = estado.gmailVista === "email" && estado.gmail.msgs.find(x => x.id === estado.gmailAberto);
  el.innerHTML = `<div class="barra-status"><span>69:67</span><span>Flamengo · 4L</span></div>` + (aberto ? gmailLeituraHtml(aberto) : gmailListaHtml(anima));
  estado.gmail.msgs.forEach(m => { m.nova = false; });
}
function gmailListaHtml(anima){
  const itens = estado.gmail.msgs.map(m => `
    <button type="button" class="gm-item${m.lida ? "" : " nao"}${m.nova ? " nova" : ""}" onclick="gmailAbrir(${m.id})">
      ${gmailAvatar()}
      <span class="gm-meio"><b>${escaparHtml(m.de)}</b><strong>${escaparHtml(m.assunto)}</strong><small>${escaparHtml(m.previa)}</small></span>
      <span class="gm-fim"><i>${zapHora(m.quando)}</i><svg viewBox="0 0 24 24"><path d="m12 3.5 2.6 5.4 5.9.8-4.3 4.1 1 5.9L12 17l-5.2 2.7 1-5.9L3.5 9.7l5.9-.8L12 3.5Z"/></svg></span>
    </button>`).join("");
  const ini = (USUARIO.nome || "Y").trim().charAt(0).toUpperCase() || "Y";
  return `<div class="${anima ? "gm-anima" : ""}" style="display:flex;flex-direction:column;flex:1;min-height:0">
    <div class="gm-topo"><svg viewBox="0 0 24 24"><path d="M4 7h16M4 12h16M4 17h16"/></svg><span class="gm-busca">Pesquisar no e-mail</span><span class="gm-av">${escaparHtml(ini)}</span></div>
    <div class="gm-rotulo">Caixa de entrada</div>
    <div class="gm-lista">${itens || `<div class="gm-vazio">Nenhum e-mail ainda.<br>Crie uma conta ou use “Esqueci minha senha” no Água em Dia: os e-mails chegam aqui.</div>`}</div>
    <button type="button" class="gm-fab" onclick="avisar('Gmail simulado','Aqui só se recebe: nenhum e-mail real é enviado.','alerta')"><svg viewBox="0 0 24 24"><path d="M4 20h4L19 9a2.8 2.8 0 0 0-4-4L4 16v4Z"/></svg>Escrever</button>
  </div>`;
}
function gmailLeituraHtml(m){
  return `<div class="gm-cab"><button type="button" class="gm-volta" onclick="gmailVoltar()" aria-label="Voltar à caixa de entrada">‹</button>
      <span class="gm-cab-ac"><svg viewBox="0 0 24 24"><path d="M4 8h16v11H4zM3 4h18v4H3zM10 12h4"/></svg><svg viewBox="0 0 24 24"><path d="M5 7h14M9 7V4h6v3M7 7l1 13h8l1-13"/></svg></span></div>
    <div class="gm-leitura">
      <h2 class="gm-assunto">${escaparHtml(m.assunto)}<span>Caixa de entrada</span></h2>
      <div class="gm-rem">${gmailAvatar()}<span><b>${escaparHtml(m.de)}</b><small>&lt;${escaparHtml(m.deEmail)}&gt;</small><em>para ${escaparHtml(m.para)}</em></span><i>${zapHora(m.quando)}</i></div>
      <div class="gm-texto">${m.corpo}<p class="gm-peq">Simulação: nenhum e-mail real é enviado.</p></div>
    </div>`;
}

/* e-mails de cada evento da conta */
function gmailBoasVindas(conta){
  const nome = escaparHtml(conta.nome.split(" ")[0]);
  const linhas = [conta.usuario ? ["Usuário", "@" + conta.usuario] : null, ["E-mail", conta.email], conta.tel && conta.tel !== "—" ? ["Celular (WhatsApp)", conta.tel] : null].filter(Boolean);
  setTimeout(() => gmailEnviar({
    para:conta.email, assunto:"Bem-vindo(a) ao Água em Dia, " + conta.nome.split(" ")[0] + "!",
    previa:"Sua conta foi criada. Veja como registrar o primeiro vazamento.",
    corpo:`<p>Olá, <b>${nome}</b>!</p>
      <p>Sua conta no <b>Água em Dia</b> foi criada. Obrigado por ajudar a cuidar da água da cidade.</p>
      <div class="gm-dados">${linhas.map(l => `<div><span>${l[0]}: </span><b>${escaparHtml(l[1])}</b></div>`).join("")}</div>
      <p>Para começar:</p>
      <ol><li>Toque no botão “+” e escolha o tipo de problema.</li><li>Marque o local no mapa e, se puder, anexe uma foto.</li><li>Acompanhe cada etapa pelo WhatsApp.</li></ol>
      <button type="button" class="gm-botao" onclick="abrirApp('agua')">Abrir o Água em Dia</button>
      <p class="gm-peq">Se você não criou esta conta, ignore este e-mail.</p>`
  }), 900);
}
function gmailCodigoRecuperacao(r, reenvio){
  const cod = r.codigo, nome = escaparHtml(r.nome.split(" ")[0]);
  setTimeout(() => gmailEnviar({
    para:r.email, assunto:"Código de recuperação: " + cod.slice(0, 3) + " " + cod.slice(3),
    previa:"Use o código " + cod + " para redefinir sua senha. Vale por 10 minutos.",
    corpo:`<p>Olá, <b>${nome}</b>.</p>
      <p>${reenvio ? "Aqui está um <b>novo código</b> (o anterior deixou de valer)." : "Recebemos um pedido para redefinir a senha da sua conta no Água em Dia."} Digite este código no app:</p>
      <div class="gm-codigo" aria-label="Código ${cod.split("").join(" ")}">${cod.split("").map(d => `<b>${d}</b>`).join("")}</div>
      <p>Ele vale por <b>10 minutos</b> e só pode ser usado uma vez. Nunca compartilhe esse código com ninguém, nem com a equipe do Água em Dia.</p>
      <button type="button" class="gm-botao sec" onclick="gmailCopiar('${cod}', this)">Copiar código</button>
      <button type="button" class="gm-botao" onclick="abrirApp('agua')">Voltar ao Água em Dia</button>
      <p class="gm-peq">Se não foi você quem pediu, pode ignorar este e-mail: sua senha continua a mesma.</p>`
  }), 900);
}
function gmailSenhaAlterada(r){
  const nome = escaparHtml(r.nome.split(" ")[0]);
  setTimeout(() => gmailEnviar({
    para:r.email, assunto:"Sua senha do Água em Dia foi alterada",
    previa:"A senha da sua conta acabou de ser redefinida.",
    corpo:`<p>Olá, <b>${nome}</b>.</p>
      <p>A senha da sua conta no Água em Dia foi redefinida agora há pouco, usando o código enviado para este e-mail.</p>
      <p>Se foi você, não precisa fazer nada. Se <b>não</b> foi, crie uma nova senha pelo “Esqueci minha senha” e avise a equipe.</p>
      <button type="button" class="gm-botao" onclick="abrirApp('agua')">Voltar ao Água em Dia</button>`
  }), 700);
}

/* ----- "sistema operacional" do celular: tela inicial, abrir e fechar apps ----- */
function abrirApp(qual){
  if(_bq.fase !== "aberto") desbloquearAgora();
  if(estado.app === qual) return;
  const fone = document.querySelector("#palcoFone .fone"), ic = document.getElementById({agua:"icoAgua", zap:"icoZap", gmail:"icoGmail"}[qual]);
  if(ic){ ic.classList.remove("pulou"); void ic.offsetWidth; ic.classList.add("pulou"); }
  const janela = document.getElementById({agua:"appAgua", zap:"appZap", gmail:"appGmail"}[qual]);
  if(fone && ic && janela){
    const f = fone.getBoundingClientRect(), r = ic.getBoundingClientRect(), esc = f.width / fone.offsetWidth || 1;
    janela.style.transformOrigin = ((r.left + r.width/2 - f.left) / esc) + "px " + ((r.top + r.height/2 - f.top) / esc) + "px";
  }
  ["appAgua","appZap","appGmail"].forEach(id => { const w = document.getElementById(id); if(w && w !== janela){ w.classList.remove("aberto"); w.setAttribute("inert",""); } });
  estado.app = qual;
  document.getElementById("soHome").classList.add("recuado");
  if(qual === "zap"){
    if(estado.zapVista === "chat"){ estado.zap.naoLidas = 0; estado.zap.msgs.forEach(m => m.lida = true); }
    atualizarBadgeZap(); zapEsconderBanner(); renderZap();
  } else if(qual === "gmail"){
    atualizarBadgeGmail(); zapEsconderBanner(); renderGmail(true);
  } else if(!window._introFeita){ window._introFeita = true; iniciarIntro(); }
  janela.removeAttribute("inert"); requestAnimationFrame(() => janela.classList.add("aberto"));
}
function irParaInicio(){
  if(estado.app === "home") return;
  ["appAgua","appZap","appGmail"].forEach(id => { const w = document.getElementById(id); if(w){ w.classList.remove("aberto"); w.setAttribute("inert",""); } });
  estado.app = "home"; if(estado.zapVista === "chat") estado.zapVista = "lista"; if(estado.gmailVista === "email"){ estado.gmailVista = "lista"; estado.gmailAberto = null; }
  document.getElementById("soHome").classList.remove("recuado");
}
function tiquePelicula(){
  const d = new Date(), h = document.getElementById("homeHora"), t = document.getElementById("homeData");
  const bh = document.getElementById("bqHora"), bd = document.getElementById("bqData");
  if(bh) bh.textContent = d.toLocaleTimeString("pt-BR", {hour:"2-digit", minute:"2-digit"});
  if(h) h.textContent = d.toLocaleTimeString("pt-BR", {hour:"2-digit", minute:"2-digit"});
  if(t){ const x = d.toLocaleDateString("pt-BR", {weekday:"long", day:"numeric", month:"long"}); t.textContent = x.charAt(0).toUpperCase() + x.slice(1); if(bd) bd.textContent = t.textContent; }
}
tiquePelicula(); setInterval(tiquePelicula, 15000);

/* ===== bloqueio de tela: abre sem senha (cadeado abre e a tela sobe) ===== */
const _bq = {fase:"travado", timers:[], auto:false};
const _semMov = () => window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
function _bqAgendar(fn, ms){ _bq.timers.push(setTimeout(fn, ms)); }
function _bqLimpar(){ _bq.timers.forEach(clearTimeout); _bq.timers = []; }
function _bqTecla(k, i){
  const b = document.getElementById("soBloqueio"); if(!b) return;
  const t = b.querySelector('.bq-tecla[data-k="' + k + '"]'); if(t){ t.classList.add("pressionada"); setTimeout(() => t.classList.remove("pressionada"), 260); }
  const p = b.querySelectorAll(".bq-pontos i")[i]; if(p) p.classList.add("cheio");
}
function digitarSenha67(){
  const b = document.getElementById("soBloqueio"); if(!b || _bq.fase !== "travado") return;
  if(_semMov()) return desbloquearAgora();
  _bq.fase = "abrindo"; _bqLimpar();
  b.classList.add("certo");
  _bqAgendar(() => {
    b.classList.add("saindo");
    const h = document.getElementById("soHome"); h.removeAttribute("inert"); h.classList.remove("chegando"); void h.offsetWidth; h.classList.add("chegando");
  }, 550);
  _bqAgendar(() => { b.classList.remove("ativo"); _bq.fase = "aberto"; }, 1300);
}
function desbloquearAgora(){
  const b = document.getElementById("soBloqueio"); if(!b) return;
  _bqLimpar(); _bq.fase = "aberto"; _bq.auto = true;
  b.classList.remove("ativo", "pede", "certo", "saindo");
  document.getElementById("soHome").removeAttribute("inert");
}
function bloquearCelular(){
  const b = document.getElementById("soBloqueio"); if(!b || _bq.fase !== "aberto") return;
  irParaInicio(); _bqLimpar();
  b.querySelectorAll(".bq-pontos i").forEach(i => i.classList.remove("cheio"));
  b.classList.remove("pede", "certo", "saindo"); b.classList.add("ativo");
  document.getElementById("soHome").setAttribute("inert", "");
  _bq.fase = "travado"; tiquePelicula();
  _bqAgendar(digitarSenha67, 1100);
}
(function(){
  const palco = document.getElementById("palcoFone"), ent = document.querySelector("#palcoFone .entalhe");
  if(ent) ent.addEventListener("click", () => { if(_bq.fase === "aberto") bloquearCelular(); });
  if(!palco) return;
  if(_semMov() || !("IntersectionObserver" in window)){ desbloquearAgora(); return; }
  new IntersectionObserver((es, ob) => {
    if(es.some(e => e.isIntersecting && e.intersectionRatio >= .5) && !_bq.auto){ _bq.auto = true; _bqAgendar(digitarSenha67, 1000); ob.disconnect(); }
  }, {threshold:[.5]}).observe(palco);
})();


/* mensagens de cada evento do chamado */
function zapChamadoRegistrado(c){
  const t = TIPOS.find(x => x.id === c.tipo);
  zapEnviar("✅ *Chamado " + c.protocolo + " registrado*\n" + t.nome + " em " + c.bairro + ". Estimativa: " + fmt(c.vazao) + " L/h.\nAvisamos aqui quando a equipe for despachada.",
    {acao:"zapIrPara('perfil')", rotuloAcao:"Ver meu chamado"});
}
function zapChamadoDespachado(c){
  zapEnviar("🚧 *Equipe a caminho!*\nDespachamos uma equipe para " + c.referencia + " (chamado " + c.protocolo + ").");
}
function zapChamadoConcluido(c){
  zapEnviar("💧 *Reparo concluído em " + c.bairro + "!*\n" + fmt(c.vazao) + " L/h pararam de escorrer. Obrigado por avisar.\nComo foi o atendimento? Avalie de 1 a 5.",
    {acao:"zapIrPara('perfil')", rotuloAcao:"Avaliar reparo"});
}
function zapBoasVindas(){
  const nome = USUARIO.nome.split(" ")[0];
  estado.zap.msgs = []; estado.zap.naoLidas = 0; atualizarBadgeZap();
  setTimeout(() => zapEnviar("Olá, " + nome + "! 👋 Aqui é o *Água em Dia*.\nSeu número " + zapNumero() + " foi confirmado. Vou avisar por aqui toda vez que um chamado seu mudar de status."), 1800);
}



/* ondinha de toque dentro do celular */
(function(){
  const fone = document.querySelector("#palcoFone .fone"); if(!fone) return;
  fone.addEventListener("pointerdown", e => {
    if(window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches) return;
    const r = fone.getBoundingClientRect(), esc = r.width / fone.offsetWidth || 1;
    const t = document.createElement("span"); t.className = "toque";
    t.style.left = ((e.clientX - r.left) / esc) + "px"; t.style.top = ((e.clientY - r.top) / esc) + "px";
    fone.appendChild(t); setTimeout(() => t.remove(), 700);
  }, {passive:true});
})();
function irParaCelular(){
  const alvo = document.getElementById("palcoFone");
  if(alvo) alvo.scrollIntoView({behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth", block:"start"});
}

/* ===== v55: texto palavra por palavra, contadores, tilt do celular ===== */
function dividirPalavras(el){
  if(el.dataset.dividido) return; el.dataset.dividido = "1";
  const alvo = [];
  (function varrer(n){ n.childNodes.forEach(c => { if(c.nodeType === 3) alvo.push(c); else if(c.nodeType === 1 && !/^(CODE|SCRIPT|STYLE)$/.test(c.tagName)) varrer(c); }); })(el);
  let i = 0;
  alvo.forEach(t => {
    const partes = t.nodeValue.split(/(\s+)/); const frag = document.createDocumentFragment();
    partes.forEach(w => {
      if(!w) return;
      if(/^\s+$/.test(w)){ frag.appendChild(document.createTextNode(" ")); return; }
      const o = document.createElement("span"); o.className = "pw"; o.setAttribute("aria-hidden","true");
      const a = document.createElement("i"); a.textContent = w; a.style.setProperty("--w", i++); o.appendChild(a); frag.appendChild(o);
    });
    t.parentNode.replaceChild(frag, t);
  });
  el.setAttribute("aria-label", el.textContent.replace(/\s+/g," ").trim());
}
function contarAte(el){
  const m = el.textContent.match(/^([\d.]+)(.*)$/); if(!m) return;
  const alvo = parseInt(m[1].replace(/\./g,""),10); if(!isFinite(alvo)) return;
  const sufixo = m[2], t0 = performance.now(), dur = 1100;
  (function passo(t){ const k = Math.min(1,(t - t0)/dur), e = 1 - Math.pow(1 - k, 3);
    el.textContent = Math.round(alvo * e).toLocaleString("pt-BR") + sufixo; if(k < 1) requestAnimationFrame(passo); })(t0);
}
function prepararTextoAnimado(){
  const reduzido = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
  document.querySelectorAll("#vista-app .notas h2, #vista-app .notas .subtitulo, #vista-app .nota h3").forEach(e => { if(!reduzido) dividirPalavras(e); });
  document.querySelectorAll("#vista-app .nota p").forEach(p => { if(!p.querySelector("code") && !reduzido) dividirPalavras(p); else p.classList.add("rv-p"); });
  const sub = document.querySelector("#vista-app .notas .subtitulo"); if(sub) sub.classList.add("rv-sub");
  document.querySelectorAll("#vista-app .tabela-vazao tr").forEach((tr,i) => tr.style.setProperty("--r", i));
  document.querySelectorAll("#vista-app .nota").forEach(n => { n.style.setProperty("--d","0ms"); });
  const esc = document.querySelector("#vista-app .como-escala");
  if(esc && "IntersectionObserver" in window && !reduzido){
    const o2 = new IntersectionObserver(es => { if(es[0].isIntersecting){ o2.disconnect(); setTimeout(() => esc.querySelectorAll(".lh").forEach(contarAte), 500); } }, {threshold:.4});
    o2.observe(esc);
  }
  // contador nos valores da tabela ao revelar
  const tab = document.querySelector("#vista-app .tabela-vazao");
  if(tab && "IntersectionObserver" in window && !reduzido){
    const o = new IntersectionObserver(es => { if(es[0].isIntersecting){ o.disconnect(); setTimeout(() => tab.querySelectorAll("td:last-child").forEach(contarAte), 450); } }, {threshold:.5});
    o.observe(tab);
  }
}
function iniciarCelular3D(){
  const fone = document.querySelector("#palcoFone .fone"); if(!fone || fone.parentNode.classList.contains("fone-3d")) return;
  const reduzido = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
  const w = document.createElement("div"); w.className = "fone-3d"; fone.parentNode.insertBefore(w, fone); w.appendChild(fone);
  if(reduzido) return;
  let alvoX = 0, alvoY = 0, atualX = 0, atualY = 0, ativo = false;
  if(false) addEventListener("pointermove", e => { alvoY = (e.clientX / innerWidth - .5) * 14; alvoX = -(e.clientY / innerHeight - .5) * 10; }, {passive:true});
  if(false)(function laco(){ atualX += (alvoX - atualX) * .08; atualY += (alvoY - atualY) * .08;
    w.style.transform = "rotateX(" + atualX.toFixed(2) + "deg) rotateY(" + atualY.toFixed(2) + "deg)"; requestAnimationFrame(laco); })();
  if("IntersectionObserver" in window){
    const o = new IntersectionObserver(es => { if(es[0].isIntersecting){ o.disconnect();  } }, {threshold:.5}); o.observe(fone);
  }
  // contadores do perfil ao desenhar
  const corpo = document.getElementById("foneCorpo");
  if(corpo && "MutationObserver" in window) new MutationObserver(() => corpo.querySelectorAll(".perfil-stat .v").forEach(v => { if(!v.dataset.c){ v.dataset.c = "1"; contarAte(v); } })).observe(corpo,{childList:true});
}

function ajustarEscalaFone(){
  const raiz = document.documentElement, topo = document.querySelector(".topo");
  const larguraMax = Math.min(1.5, (innerWidth - 24) / 398);
  let esc = 1, zt = 1;
  for(let k = 0; k < 3; k++){                       // 3 passadas: o cabeçalho muda de altura com o zoom
    raiz.style.setProperty("--zoom-pag", zt.toFixed(3));
    const hTopo = topo && getComputedStyle(topo).position === "sticky" ? topo.offsetHeight : 0;
    esc = Math.max(.5, Math.min(1.5, (innerHeight - hTopo - 40) / 816, larguraMax));
    zt = innerWidth > 760 ? Math.max(1, Math.min(1.3, 1 + (esc - 1) * .75)) : 1;   // o resto cresce junto, em proporção
  }
  raiz.style.setProperty("--escala-fone", esc.toFixed(3));
  raiz.style.setProperty("--zoom-pag", zt.toFixed(3));
  const hT = topo && getComputedStyle(topo).position === "sticky" ? topo.offsetHeight : 0;
  const pf = document.getElementById("palcoFone"); if(pf) pf.style.scrollMarginTop = (hT + 12) + "px";
}
addEventListener("resize", ajustarEscalaFone); addEventListener("load", ajustarEscalaFone);
ajustarEscalaFone();
function iniciarAnimacoesDeRolagem(){
  const reduzido = window.matchMedia && matchMedia("(prefers-reduced-motion: reduce)").matches;
  prepararTextoAnimado(); iniciarCelular3D();
  const alvos = document.querySelectorAll("#vista-app .notas > *");
  alvos.forEach((el, i) => { el.classList.add("rv"); el.style.setProperty("--d", Math.min(i, 4) * 70 + "ms"); });
  if(!("IntersectionObserver" in window) || reduzido){ alvos.forEach(el => el.classList.add("visivel")); }
  else {
    const obs = new IntersectionObserver(es => es.forEach(e => { if(e.isIntersecting){ e.target.classList.add("visivel"); obs.unobserve(e.target); } }),
      {threshold:.18, rootMargin:"0px 0px -6% 0px"});
    alvos.forEach(el => obs.observe(el));
  }
  const fone = document.querySelector("#palcoFone .fone"), palco = document.getElementById("palcoFone"), barra = document.getElementById("rolagemBarra");
  let pendente = false;
  function atualizar(){
    pendente = false;
    const h = document.documentElement.scrollHeight - innerHeight;
    const ag = document.getElementById("aguaCorpo");
    if(ag){
      const rp = palco ? palco.getBoundingClientRect() : null;
      // 0 no topo da página -> 1 quando o celular está centralizado na tela
      const alvo = rp ? Math.max(1, scrollY + rp.top + rp.height/2 - innerHeight/2) : 1;
      const nivel = reduzido ? 0.6 : Math.max(0, Math.min(1, scrollY / alvo));
      const eased = 1 - Math.pow(1 - nivel, 1.6);
      ag.style.transform = "translateY(" + ((1 - (0.2 + eased * 0.8)) * 100).toFixed(2) + "%)";
    }
    document.documentElement.style.setProperty("--rol", (h > 0 ? Math.max(0, Math.min(1, scrollY / h)) : 0).toFixed(3));
    document.body.classList.toggle("vista-op-ativa", estado.vista !== "app");
    if(barra) barra.style.width = (h > 0 ? Math.min(100, scrollY / h * 100) : 0) + "%";
    if(!fone || !palco || reduzido || palco.offsetParent === null) { if(fone) { fone.style.transform = ""; fone.style.opacity = ""; } return; }
    const r = palco.getBoundingClientRect();
    const p = Math.max(0, Math.min(1, (innerHeight - r.top) / (innerHeight * 0.85)));
    if(p >= .995){ fone.style.transform = ""; fone.style.opacity = ""; }
    else {
      fone.style.transform = "rotateX(" + ((1 - p) * 16).toFixed(2) + "deg) translateY(" + ((1 - p) * 40).toFixed(1) + "px) scale(" + (.86 + .14 * p).toFixed(3) + ")";
      fone.style.opacity = (.25 + .75 * p).toFixed(2);
    }
  }
  function pedir(){ if(!pendente){ pendente = true; requestAnimationFrame(atualizar); } }
  addEventListener("scroll", pedir, {passive:true}); addEventListener("resize", pedir);
  atualizar();
  // a intro do login só toca quando o celular aparece na tela
  if(fone && "IntersectionObserver" in window && !reduzido){
    /* a abertura (intro + login) acontece quando a pessoa abre o app Água em Dia */
  }
}
async function iniciar(){
  try{ Object.assign(CONFIG, await API.config()); }
  catch(e){ /* servidor fora do ar: o login mostra o erro quando a pessoa tentar entrar */ }
  CHAVE_MAPS = CHAVE_MAPS || CONFIG.mapsKey || "";
  GOOGLE_CLIENT_ID = CONFIG.googleClientId || "";
  if(CONFIG.demo){
    // Modo demonstração: chamados e avaliações fictícios (só nesta tela, nunca gravados no servidor)
    semear();
    setInterval(simularChegadaDeChamado, INTERVALO_NOVO_CHAMADO_MS);
    setInterval(simularAvaliacaoAleatoria, INTERVALO_AVALIACAO_MS);
  } else {
    ["aba-op","icoZap","icoGmail"].forEach(id => document.getElementById(id).classList.add("oculto"));
  }
  popularFiltroBairro();
  popularFiltroTipo();
  desenhar();
  moverIndicador();
  carregarGoogle();
}
window.addEventListener("resize", moverIndicador);
window.addEventListener("aed:sessao-expirada", () => { avisar("Sessão expirada", "Entre novamente para continuar.", "alerta"); sairDaConta(); });
ativarRespingos(".btn-1, .central, .mini.forte");
iniciarAnimacoesDeRolagem();
setInterval(tique, 1000);
iniciar();
