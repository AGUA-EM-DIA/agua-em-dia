package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.ConquistaDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConquistasTest {

    private static boolean conquistada(List<ConquistaDTO> lista, String codigo) {
        return lista.stream().filter(c -> c.codigo().equals(codigo)).findFirst().orElseThrow().conquistada();
    }

    @Test
    void usuarioNovoNaoTemConquistas() {
        List<ConquistaDTO> lista = Conquistas.calcular(0, 0, 0, 0, 0);
        assertTrue(lista.stream().noneMatch(ConquistaDTO::conquistada));
    }

    @Test
    void conquistaPorQuantidadeDeRegistros() {
        List<ConquistaDTO> lista = Conquistas.calcular(5, 0, 0, 0, 0);
        assertTrue(conquistada(lista, "PRIMEIRO_AVISO"));
        assertTrue(conquistada(lista, "OLHO_NA_RUA"));
        assertFalse(conquistada(lista, "GUARDIAO_DA_AGUA"));
    }

    @Test
    void conquistaPorApoioResolucaoAvaliacaoELitros() {
        List<ConquistaDTO> lista = Conquistas.calcular(1, 1, 5, 1, 12000);
        assertTrue(conquistada(lista, "EU_TAMBEM_VI"));
        assertTrue(conquistada(lista, "VIZINHANCA_ATENTA"));
        assertTrue(conquistada(lista, "PROBLEMA_RESOLVIDO"));
        assertTrue(conquistada(lista, "OPINIAO_QUE_CONTA"));
        assertTrue(conquistada(lista, "FAMILIA_INTEIRA"));
        assertEquals(8, lista.size());
    }
}
