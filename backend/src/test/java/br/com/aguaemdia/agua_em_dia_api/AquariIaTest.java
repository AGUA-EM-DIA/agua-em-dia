package br.com.aguaemdia.agua_em_dia_api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Aquari, identificação por foto (sem chave configurada) e configuração pública. */
class AquariIaTest extends ApiTesteBase {

    @Test
    void aquariRespondePerguntaFrequente() throws Exception {
        String token = novoCidadaoComToken();
        mvc.perform(post("/api/aquari/mensagens").header("Authorization", bearer(token))
                        .contentType(MediaType.APPLICATION_JSON).content(json("texto", texto("Como faço para registrar um vazamento?"))))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Registrar")));
    }

    @Test
    void identificacaoPorFotoFicaIndisponivelSemChaveDoGemini() throws Exception {
        String token = novoCidadaoComToken();
        byte[] jpeg = new byte[64];
        jpeg[0] = (byte) 0xFF;
        jpeg[1] = (byte) 0xD8;
        jpeg[2] = (byte) 0xFF;
        mvc.perform(multipart("/api/ia/identificar").file(new MockMultipartFile("foto", "f.jpg", "image/jpeg", jpeg))
                .header("Authorization", bearer(token))).andExpect(status().isServiceUnavailable());
    }

    @Test
    void configuracaoPublicaNaoExigeLoginENaoTrazSegredos() throws Exception {
        mvc.perform(get("/api/config"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"demo\":true")))
                .andExpect(content().string(containsString("\"iaDisponivel\":false")));
    }
}
