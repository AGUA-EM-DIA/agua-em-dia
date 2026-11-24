package br.com.aguaemdia.agua_em_dia_api;

import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.regex.Pattern;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Cadastro, login, token e recuperação de senha. */
class UsuarioFluxoTest extends ApiTesteBase {

    @Autowired
    private UsuarioRepository usuarios;

    @Test
    void cadastroComDadosValidosGravaSenhaCriptografada() throws Exception {
        String email = "maria" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "m" + sufixo())).andExpect(status().isCreated());

        Usuario salvo = usuarios.findByEmail(email).orElseThrow();
        assertNotEquals(SENHA, salvo.getSenha());
        assertTrue(salvo.getSenha().startsWith("$2"), "a senha deve estar em BCrypt");
        assertTrue(salvo.getCpf().matches("\\d{11}"), "o CPF é gravado só com dígitos");
        assertTrue(salvo.getTermosAceitosEm() != null, "o aceite dos termos deve ficar registrado");
    }

    @Test
    void cadastroComDadosInvalidosRetorna400() throws Exception {
        String suf = sufixo();
        // e-mail inválido
        cadastrar(corpoCadastro("isso-nao-e-email", cpfValido(), "u" + suf)).andExpect(status().isBadRequest());
        // CPF com dígito verificador errado
        cadastrar(corpoCadastro("a" + suf + "@exemplo.com", "12345678901", "u" + suf)).andExpect(status().isBadRequest());
        // sem aceitar os termos
        String semTermos = corpoCadastro("b" + suf + "@exemplo.com", cpfValido(), "v" + suf).replace("\"aceitouTermos\":true", "\"aceitouTermos\":false");
        cadastrar(semTermos).andExpect(status().isBadRequest());
        // senha curta
        String senhaCurta = corpoCadastro("c" + suf + "@exemplo.com", cpfValido(), "w" + suf).replace(SENHA, "123");
        cadastrar(senhaCurta).andExpect(status().isBadRequest());
    }

    @Test
    void cpfDuplicadoRetorna409() throws Exception {
        String cpf = cpfValido();
        cadastrar(corpoCadastro("um" + sufixo() + "@exemplo.com", cpf, "a" + sufixo())).andExpect(status().isCreated());
        cadastrar(corpoCadastro("dois" + sufixo() + "@exemplo.com", cpf, "b" + sufixo())).andExpect(status().isConflict());
    }

    @Test
    void emailDuplicadoRetorna409() throws Exception {
        String email = "igual" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "a" + sufixo())).andExpect(status().isCreated());
        cadastrar(corpoCadastro(email.toUpperCase(), cpfValido(), "b" + sufixo())).andExpect(status().isConflict());
    }

    @Test
    void loginComEmailESenhaCorretosRetornaToken() throws Exception {
        String email = "login" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "l" + sufixo())).andExpect(status().isCreated());
        entrar(email, SENHA)
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("\"token\"")))
                .andExpect(content().string(containsString(email)));
    }

    @Test
    void loginComSenhaIncorretaRetorna401() throws Exception {
        String email = "errada" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "e" + sufixo())).andExpect(status().isCreated());
        entrar(email, "SenhaErrada99").andExpect(status().isUnauthorized());
    }

    @Test
    void loginComCpfNaoEhPermitido() throws Exception {
        String cpf = cpfValido();
        cadastrar(corpoCadastro("cpf" + sufixo() + "@exemplo.com", cpf, "c" + sufixo())).andExpect(status().isCreated());
        entrar(cpf, SENHA).andExpect(status().isBadRequest());
    }

    @Test
    void rotaPrivadaSemTokenOuComTokenInvalidoRetorna401() throws Exception {
        mvc.perform(get("/api/usuarios/me")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/usuarios/me").header("Authorization", "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("mensagem")));
    }

    @Test
    void rotaPrivadaComTokenValidoRetornaOsDadosDoCidadao() throws Exception {
        String token = novoCidadaoComToken();
        mvc.perform(get("/api/usuarios/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("cpfMascarado")));
    }

    @Test
    void recuperacaoDeSenhaComCodigoCorreto() throws Exception {
        String email = "rec" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "r" + sufixo())).andExpect(status().isCreated());

        // No modo demonstração (app.demo=true nos testes) o código volta na resposta
        MvcResult pedido = mvc.perform(post("/api/auth/recuperacao").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", texto(email)))).andExpect(status().isOk()).andReturn();
        String codigo = extrair(Pattern.compile("\"codigoDemonstracao\"\\s*:\\s*\"(\\d{6})\""), pedido.getResponse().getContentAsString());

        String novaSenha = "NovaSenha567";
        mvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", texto(email), "codigo", texto(codigo), "novaSenha", texto(novaSenha))))
                .andExpect(status().isOk());

        entrar(email, novaSenha).andExpect(status().isOk());
        entrar(email, SENHA).andExpect(status().isUnauthorized());
        // o código só vale uma vez
        mvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", texto(email), "codigo", texto(codigo), "novaSenha", texto("OutraSenha789"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void recuperacaoBloqueiaDepoisDeCincoCodigosErrados() throws Exception {
        String email = "bloq" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "b" + sufixo())).andExpect(status().isCreated());
        MvcResult pedido = mvc.perform(post("/api/auth/recuperacao").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", texto(email)))).andReturn();
        String certo = extrair(Pattern.compile("\"codigoDemonstracao\"\\s*:\\s*\"(\\d{6})\""), pedido.getResponse().getContentAsString());
        String errado = certo.equals("111111") ? "222222" : "111111";

        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                    .content(json("email", texto(email), "codigo", texto(errado), "novaSenha", texto("NovaSenha567"))))
                    .andExpect(status().isBadRequest());
        }
        // mesmo com o código certo, depois de 5 erros é preciso pedir outro
        mvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", texto(email), "codigo", texto(certo), "novaSenha", texto("NovaSenha567"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void recuperacaoNaoRevelaSeOEmailTemCadastro() throws Exception {
        mvc.perform(post("/api/auth/recuperacao").contentType(MediaType.APPLICATION_JSON)
                        .content(json("email", texto("ninguem" + sufixo() + "@exemplo.com"))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Se esse e-mail tiver cadastro")));
    }
}
