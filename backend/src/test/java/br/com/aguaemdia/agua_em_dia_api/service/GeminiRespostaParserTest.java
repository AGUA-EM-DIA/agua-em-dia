package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.IdentificacaoDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeminiRespostaParserTest {

    @Test
    void leRespostaValida() {
        String json = "{\"tipo\":\"REDE\",\"porte\":\"MEDIO\",\"confianca\":0.82,"
                + "\"motivo\":\"Água brotando do asfalto, com \\\"poça\\\" na rua.\"}";
        IdentificacaoDTO r = GeminiRespostaParser.interpretar(json).orElseThrow();
        assertEquals("REDE", r.tipo());
        assertEquals("MEDIO", r.porte());
        assertEquals(0.82, r.confianca(), 0.0001);
        assertEquals("Água brotando do asfalto, com \"poça\" na rua.", r.motivo());
    }

    @Test
    void aceitaCercaDeCodigoEUnicode() {
        String json = "```json\n{\"tipo\": \"NENHUM\", \"porte\": \"FIO\", \"confianca\": 1,"
                + " \"motivo\": \"N\\u00e3o h\\u00e1 vazamento.\"}\n```";
        IdentificacaoDTO r = GeminiRespostaParser.interpretar(json).orElseThrow();
        assertEquals("NENHUM", r.tipo());
        assertEquals("Não há vazamento.", r.motivo());
    }

    @Test
    void limitaConfiancaEMotivo() {
        String longo = "a".repeat(300);
        String json = "{\"tipo\":\"RAMAL\",\"porte\":\"FIO\",\"confianca\":7.5,\"motivo\":\"" + longo + "\"}";
        IdentificacaoDTO r = GeminiRespostaParser.interpretar(json).orElseThrow();
        assertEquals(1.0, r.confianca(), 0.0001);
        assertEquals(140, r.motivo().length());
    }

    @Test
    void descartaValoresForaDoEsperado() {
        assertTrue(GeminiRespostaParser.interpretar("{\"tipo\":\"OUTRO\",\"porte\":\"FIO\",\"confianca\":0.5}").isEmpty());
        assertTrue(GeminiRespostaParser.interpretar("{\"tipo\":\"REDE\",\"porte\":\"GIGANTE\",\"confianca\":0.5}").isEmpty());
        assertTrue(GeminiRespostaParser.interpretar("{\"tipo\":\"REDE\",\"porte\":\"FIO\"}").isEmpty());
        assertTrue(GeminiRespostaParser.interpretar("texto sem json").isEmpty());
        assertTrue(GeminiRespostaParser.interpretar(null).isEmpty());
    }
}
