package br.com.aguaemdia.agua_em_dia_api.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/** Todas as datas do sistema são gravadas em UTC e enviadas ao frontend em milissegundos. */
public final class Tempo {

    private Tempo() {
    }

    public static LocalDateTime agora() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static Long paraMillis(LocalDateTime data) {
        return data == null ? null : data.toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
