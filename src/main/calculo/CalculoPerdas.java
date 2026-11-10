package br.com.aguaemdia.calculo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cálculo puro (sem Spring/banco) do volume perdido: Σ vazão (L/h) × duração (h) de intervalos
 * não sobrepostos (RN-AD07). Estimativa, não medição.
 */
public final class CalculoPerdas {
    private CalculoPerdas() {}

    private static final BigDecimal MS_POR_HORA = BigDecimal.valueOf(3_600_000L);

    /** fim == null significa "ainda em curso"; vazaoLH == null significa vazão não confiável. */
    public record Intervalo(OffsetDateTime inicio, OffsetDateTime fim, BigDecimal vazaoLH) {}

    /** incerto = existe intervalo no recorte sem vazão confiável (não entra na soma). */
    public record Resultado(BigDecimal litros, boolean incerto) {
        public static Resultado zero() { return new Resultado(BigDecimal.ZERO.setScale(4), false); }
        public Resultado somar(Resultado o) { return new Resultado(litros.add(o.litros), incerto || o.incerto); }
    }

    public static BigDecimal horas(OffsetDateTime de, OffsetDateTime ate) {
        long ms = Duration.between(de, ate).toMillis();
        return BigDecimal.valueOf(ms).divide(MS_POR_HORA, 10, RoundingMode.HALF_UP);
    }

    private static OffsetDateTime maior(OffsetDateTime a, OffsetDateTime b) { return a.isAfter(b) ? a : b; }
    private static OffsetDateTime menor(OffsetDateTime a, OffsetDateTime b) { return a.isBefore(b) ? a : b; }

    /** Volume de um intervalo recortado pela janela [de, ate]. Intervalo aberto termina em "agora". */
    public static Resultado volume(Intervalo i, OffsetDateTime de, OffsetDateTime ate, OffsetDateTime agora) {
        OffsetDateTime fimEfetivo = i.fim() == null ? agora : i.fim();
        OffsetDateTime ini = maior(i.inicio(), de);
        OffsetDateTime fim = menor(fimEfetivo, ate);
        if (!fim.isAfter(ini)) return Resultado.zero();
        if (i.vazaoLH() == null) return new Resultado(BigDecimal.ZERO.setScale(4), true);
        return new Resultado(i.vazaoLH().multiply(horas(ini, fim)).setScale(4, RoundingMode.HALF_UP), false);
    }

    public static Resultado volume(List<Intervalo> intervalos, OffsetDateTime de, OffsetDateTime ate, OffsetDateTime agora) {
        Resultado total = Resultado.zero();
        for (Intervalo i : intervalos) total = total.somar(volume(i, de, ate, agora));
        return total;
    }

    /** Litros por dia civil local (CT-AD13: intervalo que atravessa a meia-noite é repartido). */
    public static Map<LocalDate, BigDecimal> volumePorDia(List<Intervalo> intervalos, LocalDate primeiroDia,
            LocalDate ultimoDia, ZoneId zona, OffsetDateTime agora) {
        Map<LocalDate, BigDecimal> mapa = new LinkedHashMap<>();
        List<LocalDate> dias = new ArrayList<>();
        for (LocalDate d = primeiroDia; !d.isAfter(ultimoDia); d = d.plusDays(1)) dias.add(d);
        for (LocalDate d : dias) {
            OffsetDateTime de = d.atStartOfDay(zona).toOffsetDateTime();
            OffsetDateTime ate = d.plusDays(1).atStartOfDay(zona).toOffsetDateTime();
            mapa.put(d, volume(intervalos, de, ate, agora).litros());
        }
        return mapa;
    }
}
