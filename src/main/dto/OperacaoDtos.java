package br.com.aguaemdia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class OperacaoDtos {
    private OperacaoDtos() {}

    public record CandidatoDuplicidade(Long vazamentoId, BigDecimal distanciaMetros, String status,
            OffsetDateTime primeiroRelatoEm, long relatos, long cidadaos) {}

    public record ConfirmarDuplicidadeRequest(@NotNull Long origemId, @NotNull Long canonicoId,
            @NotBlank @Size(max = 1000) String justificativa) {}

    public record MotivoRequest(@NotBlank @Size(max = 1000) String motivo) {}

    public record VinculoResponse(Long id, Long origemId, Long canonicoId, OffsetDateTime associadoEm,
            OffsetDateTime revertidoEm, int relatosMovidos) {}

    public record TriagemRequest(@NotBlank @Size(max = 500) String motivo) {}

    public record ClassificacaoRequest(@NotNull Long porteId, Long infraestruturaId, @Size(max = 500) String motivo) {}

    public record TriagemResponse(Long id, Long vazamentoId, String situacao, String motivo,
            OffsetDateTime criadoEm, OffsetDateTime concluidoEm) {}

    public record SinalizarEmergenciaRequest(@NotBlank @Size(max = 1000) String motivo) {}

    public record AvaliarEmergenciaRequest(@NotBlank String decisao, Short gravidade,
            @NotBlank @Size(max = 1000) String justificativa) {}

    public record SinalizacaoResponse(Long id, Long vazamentoId, String motivo, OffsetDateTime sinalizadoEm,
            String statusOcorrencia) {}

    public record AvaliacaoEmergenciaResponse(Long id, Long vazamentoId, String decisao, Short gravidade,
            OffsetDateTime avaliadoEm) {}

    /** Linha das listas operacionais (fila, busca, mapa). */
    public record VazamentoResumo(Long id, String status, String categoria, BigDecimal latitude, BigDecimal longitude,
            String endereco, String bairro, OffsetDateTime primeiroRelatoEm, Integer prazoEfetivoHoras,
            BigDecimal prioridade, BigDecimal vazaoLitrosPorHora, boolean vazaoIncerta, long cidadaos,
            boolean atrasado, Short emergenciaGravidade, OffsetDateTime emergenciaDesde) {}

    public record FiltroVazamentos(List<String> status, String bairro, String rua, String categoria, String protocolo) {}
}
