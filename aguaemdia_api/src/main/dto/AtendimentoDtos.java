package br.com.aguaemdia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class AtendimentoDtos {
    private AtendimentoDtos() {}

    public record AtribuirEquipeRequest(@NotNull Long equipeId, @Size(max = 500) String motivo) {}

    public record RegistroExecucaoRequest(@NotBlank String tipoEvento, @Size(max = 2000) String observacao) {}

    public record ConclusaoRequest(@NotBlank @Size(min = 10, max = 4000) String resumo, @NotNull OffsetDateTime reparoEfetivoEm) {}

    public record ValidacaoRequest(@NotBlank String decisao, @Size(max = 1000) String justificativa) {}

    public record ReaberturaRequest(@NotBlank @Size(max = 1000) String motivo) {}

    public record DecidirReaberturaRequest(@NotNull Boolean aprovar, @Size(max = 1000) String justificativa) {}

    public record AtendimentoItem(Long id, Long vazamentoId, String status, OffsetDateTime criadoEm, String equipe) {}

    public record AtribuicaoItem(Long equipeId, String equipe, OffsetDateTime atribuidoEm, OffsetDateTime encerradoEm, String motivoSubstituicao) {}

    public record ExecucaoItem(String tipoEvento, OffsetDateTime registradoEm, String observacao) {}

    public record ConclusaoItem(String resumo, OffsetDateTime reparoEfetivoEm, OffsetDateTime informadoEm, String validacao) {}

    public record AtendimentoDetalhe(Long id, Long vazamentoId, String status, String statusOcorrencia, OffsetDateTime criadoEm,
            OffsetDateTime iniciadoEm, OffsetDateTime encerradoEm, String categoria, BigDecimal latitude, BigDecimal longitude,
            String endereco, String bairro, Integer prazoEfetivoHoras, BigDecimal prioridade, String equipeAtual,
            List<AtribuicaoItem> atribuicoes, List<ExecucaoItem> execucoes, List<ConclusaoItem> conclusoes, List<Long> imagens) {}

    public record SolicitacaoReaberturaItem(Long id, Long vazamentoId, String motivo, String situacao, OffsetDateTime criadoEm) {}
}
