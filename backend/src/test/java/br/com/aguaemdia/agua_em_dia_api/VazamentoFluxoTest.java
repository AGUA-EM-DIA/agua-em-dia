package br.com.aguaemdia.agua_em_dia_api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Registro, consulta, edição, reforço, avaliação e situação dos vazamentos. */
class VazamentoFluxoTest extends ApiTesteBase {

    private static final String DADOS_VALIDOS = json("tipo", texto("REDE"), "porte", texto("MEDIO"),
            "descricao", texto("Água brotando do asfalto"), "endereco", texto("Av. Recife, 100"),
            "latitude", "-8.0745", "longitude", "-34.9110");

    /** Começa com os bytes de um JPEG; o servidor confere o conteúdo, não o nome do arquivo. */
    private static byte[] jpegDeTeste() {
        byte[] b = new byte[64];
        b[0] = (byte) 0xFF;
        b[1] = (byte) 0xD8;
        b[2] = (byte) 0xFF;
        b[3] = (byte) 0xE0;
        return b;
    }

    private ResultActions registrar(String token, String dados, MockMultipartFile foto) throws Exception {
        var pedido = multipart("/api/vazamentos")
                .file(new MockMultipartFile("dados", "", MediaType.APPLICATION_JSON_VALUE, dados.getBytes()))
                .header("Authorization", bearer(token));
        if (foto != null) {
            pedido.file(foto);
        }
        return mvc.perform(pedido);
    }

    private String registrarEPegarId(String token) throws Exception {
        MvcResult r = registrar(token, DADOS_VALIDOS, null).andExpect(status().isCreated()).andReturn();
        return extrairId(r);
    }

    private ResultActions mudarSituacao(String token, String id, String situacao) throws Exception {
        return mvc.perform(patch("/api/vazamentos/" + id + "/situacao").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON).content(json("situacao", texto(situacao))));
    }

    @Test
    void registrarVazamentoSemFotoCalculaProtocoloEPerda() throws Exception {
        String token = novoCidadaoComToken();
        registrar(token, DADOS_VALIDOS, null)
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"vazaoLitrosHora\":960")))   // REDE (1,6) x MEDIO (600)
                .andExpect(content().string(containsString("\"situacao\":\"ABERTO\"")))
                .andExpect(content().string(containsString("\"meu\":true")))
                .andExpect(content().string(containsString("\"protocolo\":\"RE-")));
    }

    @Test
    void registrarComFotoValidaGuardaOEnderecoDaImagem() throws Exception {
        String token = novoCidadaoComToken();
        MockMultipartFile foto = new MockMultipartFile("foto", "vazamento.jpg", "image/jpeg", jpegDeTeste());
        registrar(token, DADOS_VALIDOS, foto)
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("\"fotoUrl\":\"/uploads/")));
    }

    @Test
    void fotoInvalidaEhRecusadaMesmoComNomeDeImagem() throws Exception {
        String token = novoCidadaoComToken();
        MockMultipartFile falsa = new MockMultipartFile("foto", "vazamento.jpg", "image/jpeg", "%PDF-1.7 isto nao e uma foto".getBytes());
        registrar(token, DADOS_VALIDOS, falsa).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void coordenadasInvalidasRetornam400() throws Exception {
        String token = novoCidadaoComToken();
        String latitudeInvalida = DADOS_VALIDOS.replace("-8.0745", "95");
        registrar(token, latitudeInvalida, null).andExpect(status().isBadRequest());
    }

    @Test
    void registrarSemTokenRetorna401() throws Exception {
        mvc.perform(multipart("/api/vazamentos")
                        .file(new MockMultipartFile("dados", "", MediaType.APPLICATION_JSON_VALUE, DADOS_VALIDOS.getBytes())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cidadaoVeSeusRegistrosEOsDeTodosNoMapa() throws Exception {
        String autor = novoCidadaoComToken();
        String outro = novoCidadaoComToken();
        String id = registrarEPegarId(autor);

        mvc.perform(get("/api/vazamentos/meus").header("Authorization", bearer(autor)))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"id\":" + id)));
        mvc.perform(get("/api/vazamentos/meus").header("Authorization", bearer(outro)))
                .andExpect(status().isOk()).andExpect(content().string(not(containsString("\"id\":" + id))));
        mvc.perform(get("/api/vazamentos").header("Authorization", bearer(outro)))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"id\":" + id)));
        mvc.perform(get("/api/vazamentos/" + id).header("Authorization", bearer(outro)))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"meu\":false")));
    }

    @Test
    void soODonoEditaADescricao() throws Exception {
        String dono = novoCidadaoComToken();
        String outro = novoCidadaoComToken();
        String id = registrarEPegarId(dono);

        mvc.perform(patch("/api/vazamentos/" + id + "/descricao").header("Authorization", bearer(dono))
                        .contentType(MediaType.APPLICATION_JSON).content(json("descricao", texto("Descrição nova"))))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Descrição nova")));
        mvc.perform(patch("/api/vazamentos/" + id + "/descricao").header("Authorization", bearer(outro))
                        .contentType(MediaType.APPLICATION_JSON).content(json("descricao", texto("Invasão"))))
                .andExpect(status().isForbidden());
    }

    @Test
    void reforcoValeUmaVezEExcluiOProprioAutor() throws Exception {
        String dono = novoCidadaoComToken();
        String outro = novoCidadaoComToken();
        String id = registrarEPegarId(dono);

        mvc.perform(post("/api/vazamentos/" + id + "/apoios").header("Authorization", bearer(outro)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"apoios\":1")))
                .andExpect(content().string(containsString("\"apoiadoPorMim\":true")));
        mvc.perform(post("/api/vazamentos/" + id + "/apoios").header("Authorization", bearer(outro)))
                .andExpect(status().isConflict());
        mvc.perform(post("/api/vazamentos/" + id + "/apoios").header("Authorization", bearer(dono)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void avaliacaoSoDepoisDeResolvidoEUmaVez() throws Exception {
        String dono = novoCidadaoComToken();
        String outro = novoCidadaoComToken();
        String id = registrarEPegarId(dono);
        String nota5 = json("nota", "5", "comentario", texto("Rápido e educado"));

        // antes de resolver
        mvc.perform(post("/api/vazamentos/" + id + "/avaliacao").header("Authorization", bearer(dono))
                .contentType(MediaType.APPLICATION_JSON).content(nota5)).andExpect(status().isConflict());

        mudarSituacao(dono, id, "REPARO").andExpect(status().isOk())
                .andExpect(content().string(containsString("\"novidadeNaoVista\":true")));
        mudarSituacao(dono, id, "RESOLVIDO").andExpect(status().isOk());

        // nota fora do intervalo e avaliação de outro cidadão
        mvc.perform(post("/api/vazamentos/" + id + "/avaliacao").header("Authorization", bearer(dono))
                .contentType(MediaType.APPLICATION_JSON).content(json("nota", "6"))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/vazamentos/" + id + "/avaliacao").header("Authorization", bearer(outro))
                .contentType(MediaType.APPLICATION_JSON).content(nota5)).andExpect(status().isForbidden());

        // avaliação válida e duplicada
        mvc.perform(post("/api/vazamentos/" + id + "/avaliacao").header("Authorization", bearer(dono))
                        .contentType(MediaType.APPLICATION_JSON).content(nota5))
                .andExpect(status().isCreated()).andExpect(content().string(containsString("\"nota\":5")));
        mvc.perform(post("/api/vazamentos/" + id + "/avaliacao").header("Authorization", bearer(dono))
                .contentType(MediaType.APPLICATION_JSON).content(nota5)).andExpect(status().isConflict());
    }

    @Test
    void situacaoSoAvancaEExigePermissao() throws Exception {
        String dono = novoCidadaoComToken();
        String id = registrarEPegarId(dono);

        // sem token e sem chave de serviço
        mvc.perform(patch("/api/vazamentos/" + id + "/situacao").contentType(MediaType.APPLICATION_JSON)
                .content(json("situacao", texto("REPARO")))).andExpect(status().isUnauthorized());
        // com a chave de serviço (sistema autorizado)
        mvc.perform(patch("/api/vazamentos/" + id + "/situacao").header("X-Chave-Servico", "chave-de-servico-dos-testes")
                        .contentType(MediaType.APPLICATION_JSON).content(json("situacao", texto("REPARO"))))
                .andExpect(status().isOk()).andExpect(content().string(containsString("\"situacao\":\"REPARO\"")));
        // chave errada
        mvc.perform(patch("/api/vazamentos/" + id + "/situacao").header("X-Chave-Servico", "chave-errada")
                .contentType(MediaType.APPLICATION_JSON).content(json("situacao", texto("RESOLVIDO")))).andExpect(status().isUnauthorized());
        // não volta
        mudarSituacao(dono, id, "ABERTO").andExpect(status().isConflict());
    }

    @Test
    void estatisticasEConquistasAcompanhamOsRegistros() throws Exception {
        String token = novoCidadaoComToken();
        registrarEPegarId(token);
        mvc.perform(get("/api/usuarios/me/estatisticas").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"registrados\":1")))
                .andExpect(content().string(containsString("PRIMEIRO_AVISO")));
    }

    @Test
    void vazamentoInexistenteRetorna404() throws Exception {
        String token = novoCidadaoComToken();
        mvc.perform(get("/api/vazamentos/999999999").header("Authorization", bearer(token))).andExpect(status().isNotFound());
    }
}
