package br.com.aguaemdia.calculo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * P = Q × (1 + A) × (1 + R) × (1 + G)   (RN-AD09 — fórmula de TESTE, não homologada).
 *  A = min(1, max(0, (T − M) / M))            atraso, T = horas decorridas, M = prazo efetivo
 *  R = min(bônus × max(N − 1, 0), teto)       cidadãos distintos
 *  G = min(bônus × C, teto)                   vizinhos independentes ativos
 */
public final class FormulaPrioridade {
    private FormulaPrioridade() {}

    public record Parametros(BigDecimal bonusCidadao, BigDecimal tetoRelatos,
                             BigDecimal bonusVizinho, BigDecimal tetoConcentracao) {
        public static Parametros padrao() {
            return new Parametros(new BigDecimal("0.05"), new BigDecimal("0.30"),
                    new BigDecimal("0.05"), new BigDecimal("0.20"));
        }
    }

    public static BigDecimal fatorAtraso(BigDecimal horasDecorridas, Integer prazoHoras) {
        if (prazoHoras == null || prazoHoras <= 0 || horasDecorridas == null) return BigDecimal.ZERO.setScale(6);
        BigDecimal m = BigDecimal.valueOf(prazoHoras);
        BigDecimal a = horasDecorridas.subtract(m).divide(m, 6, RoundingMode.HALF_UP);
        if (a.signum() < 0) a = BigDecimal.ZERO;
        return a.min(BigDecimal.ONE).setScale(6, RoundingMode.HALF_UP);
    }

    public static BigDecimal fatorRelatos(int cidadaosDistintos, Parametros p) {
        int extras = Math.max(cidadaosDistintos - 1, 0);
        return p.bonusCidadao().multiply(BigDecimal.valueOf(extras)).min(p.tetoRelatos()).setScale(6, RoundingMode.HALF_UP);
    }

    public static BigDecimal fatorConcentracao(int vizinhos, Parametros p) {
        int c = Math.max(vizinhos, 0);
        return p.bonusVizinho().multiply(BigDecimal.valueOf(c)).min(p.tetoConcentracao()).setScale(6, RoundingMode.HALF_UP);
    }

    /** Sem vazão confiável (q == null) não existe pontuação: ausência não equivale a zero. */
    public static BigDecimal pontuacao(BigDecimal q, BigDecimal a, BigDecimal r, BigDecimal g) {
        if (q == null) return null;
        return q.multiply(BigDecimal.ONE.add(a)).multiply(BigDecimal.ONE.add(r)).multiply(BigDecimal.ONE.add(g))
                .setScale(6, RoundingMode.HALF_UP);
    }
}
