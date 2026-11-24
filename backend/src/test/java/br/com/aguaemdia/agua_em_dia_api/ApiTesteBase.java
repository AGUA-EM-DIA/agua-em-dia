package br.com.aguaemdia.agua_em_dia_api;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base dos testes da API: sobe a aplicação inteira, com o MySQL de teste, e chama as rotas de verdade
 * (incluindo a segurança JWT). Cada teste roda dentro de uma transação que é desfeita no final.
 */
@SpringBootTest
@Transactional
public abstract class ApiTesteBase {

    protected static final String SENHA = "Senha1234";
    private static final Pattern TOKEN = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern ID = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");

    @Autowired
    private WebApplicationContext contexto;

    protected MockMvc mvc;

    @BeforeEach
    void montarMockMvc() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    }

    /** Gera um CPF válido (com dígitos verificadores corretos) diferente a cada chamada. */
    protected static String cpfValido() {
        int[] d = new int[11];
        for (int i = 0; i < 9; i++) {
            d[i] = ThreadLocalRandom.current().nextInt(10);
        }
        d[0] = Math.max(d[0], 1); // evita sequência repetida
        d[9] = digito(d, 9);
        d[10] = digito(d, 10);
        StringBuilder sb = new StringBuilder();
        for (int x : d) {
            sb.append(x);
        }
        return sb.toString();
    }

    private static int digito(int[] d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += d[i] * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }

    protected static String sufixo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    protected static String json(String... paresChaveValor) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < paresChaveValor.length; i += 2) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(paresChaveValor[i]).append("\":").append(paresChaveValor[i + 1]);
        }
        return sb.append("}").toString();
    }

    protected static String texto(String valor) {
        return "\"" + valor + "\"";
    }

    protected String corpoCadastro(String email, String cpf, String usuario) {
        return json("nome", texto("Maria Silva"), "nomeUsuario", texto(usuario), "cpf", texto(cpf),
                "email", texto(email), "telefone", texto("(81) 99999-0000"), "senha", texto(SENHA),
                "aceitouTermos", "true");
    }

    protected ResultActions cadastrar(String corpo) throws Exception {
        return mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    protected ResultActions entrar(String email, String senha) throws Exception {
        return mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("email", texto(email), "senha", texto(senha))));
    }

    /** Cadastra um cidadão novo e devolve o token de acesso dele. */
    protected String novoCidadaoComToken() throws Exception {
        String email = "teste" + sufixo() + "@exemplo.com";
        cadastrar(corpoCadastro(email, cpfValido(), "u" + sufixo())).andReturn();
        MvcResult login = entrar(email, SENHA).andReturn();
        return extrair(TOKEN, login.getResponse().getContentAsString());
    }

    protected static String extrairId(MvcResult resultado) throws Exception {
        return extrair(ID, resultado.getResponse().getContentAsString());
    }

    protected static String extrair(Pattern padrao, String texto) {
        Matcher m = padrao.matcher(texto);
        if (!m.find()) {
            throw new AssertionError("Não achei o valor esperado na resposta: " + texto);
        }
        return m.group(1);
    }

    protected static String bearer(String token) {
        return "Bearer " + token;
    }
}
