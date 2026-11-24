package br.com.aguaemdia.agua_em_dia_api.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TipoImagemTest {

    private static byte[] comecaCom(int... bytes) {
        byte[] b = new byte[32];
        for (int i = 0; i < bytes.length; i++) {
            b[i] = (byte) bytes[i];
        }
        return b;
    }

    @Test
    void reconheceJpegPngEWebp() {
        assertEquals(TipoImagem.JPEG, TipoImagem.detectar(comecaCom(0xFF, 0xD8, 0xFF, 0xE0)).orElseThrow());
        assertEquals(TipoImagem.PNG, TipoImagem.detectar(comecaCom(0x89, 'P', 'N', 'G')).orElseThrow());
        assertEquals(TipoImagem.WEBP,
                TipoImagem.detectar(comecaCom('R', 'I', 'F', 'F', 0, 0, 0, 0, 'W', 'E', 'B', 'P')).orElseThrow());
    }

    @Test
    void recusaOutrosArquivos() {
        byte[] pdf = "%PDF-1.7 conteudo qualquer".getBytes();
        byte[] script = "<?php echo 1; ?> texto".getBytes();
        assertTrue(TipoImagem.detectar(pdf).isEmpty());
        assertTrue(TipoImagem.detectar(script).isEmpty());
        assertTrue(TipoImagem.detectar(new byte[3]).isEmpty());
        assertTrue(TipoImagem.detectar(null).isEmpty());
    }
}
