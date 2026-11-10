package br.com.aguaemdia.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public final class PainelDtos {
    private PainelDtos() {}

    public record PeriodoDto(OffsetDateTime inicio, OffsetDateTime fim) {}

    public record Volume(BigDecimal consolidado, BigDecimal provisorio, BigDecimal total, boolean possuiVazaoIncerta) {}

    /** Todos os valores de vazão e volume são ESTIMATIVAS (RN-AD07), nunca medições. */
    public record Indicadores(PeriodoDto periodo, BigDecimal vazaoEmCursoLitrosPorHora, BigDecimal vazaoProvisoriaLitrosPorHora,
            Volume volumePerdidoLitros, long ocorrenciasPendentes, long ocorrenciasProvisorias, long ocorrenciasAtrasadas,
            long reparosNoPeriodo, BigDecimal economiaPotencial30DiasLitros, BigDecimal equivalenteFamiliasMes, String observacao) {}

    public record SerieDia(LocalDate dia, BigDecimal litros, BigDecimal vazaoMediaLitrosPorHora) {}

    public record ItemRanking(String nome, BigDecimal litros, long ocorrencias) {}

    public record PontoMapa(OperacaoDtos.VazamentoResumo ocorrencia, String faixaPrioridade) {}

    public record IndicadoresPublicos(PeriodoDto periodo, BigDecimal volumePerdidoLitros, long reparosConcluidos,
            long ocorrenciasAtivas, BigDecimal equivalenteFamiliasMes, String observacao) {}
}
