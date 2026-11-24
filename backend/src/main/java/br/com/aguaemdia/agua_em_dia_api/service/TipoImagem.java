package br.com.aguaemdia.agua_em_dia_api.service;

import java.util.Optional;

/** Tipos de imagem aceitos. O tipo é descoberto pelos primeiros bytes do arquivo, não pelo nome nem pelo Content-Type. */
public enum TipoImagem {
    JPEG("image/jpeg", "jpg"),
    PNG("image/png", "png"),
    WEBP("image/webp", "webp");

    private final String mime;
    private final String extensao;

    TipoImagem(String mime, String extensao) {
        this.mime = mime;
        this.extensao = extensao;
    }

    public String getMime() {
        return mime;
    }

    public String getExtensao() {
        return extensao;
    }

    public static Optional<TipoImagem> detectar(byte[] b) {
        if (b == null || b.length < 12) {
            return Optional.empty();
        }
        if ((b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return Optional.of(JPEG);
        }
        if ((b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return Optional.of(PNG);
        }
        if (b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') {
            return Optional.of(WEBP);
        }
        return Optional.empty();
    }
}
