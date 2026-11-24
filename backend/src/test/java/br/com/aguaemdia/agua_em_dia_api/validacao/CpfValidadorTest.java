package br.com.aguaemdia.agua_em_dia_api.validacao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CpfValidadorTest {

    @Test
    void aceitaCpfValidoComEmSemPontuacao() {
        assertTrue(CpfValidador.cpfValido("52998224725"));
        assertTrue(CpfValidador.cpfValido("529.982.247-25"));
    }

    @Test
    void recusaDigitoVerificadorErrado() {
        assertFalse(CpfValidador.cpfValido("52998224726"));
        assertFalse(CpfValidador.cpfValido("12345678901"));
    }

    @Test
    void recusaSequenciaRepetidaETamanhoErrado() {
        assertFalse(CpfValidador.cpfValido("11111111111"));
        assertFalse(CpfValidador.cpfValido("123"));
        assertFalse(CpfValidador.cpfValido(null));
    }

    @Test
    void apenasDigitosRemovePontuacao() {
        assertEquals("52998224725", CpfValidador.apenasDigitos("529.982.247-25"));
    }
}
