package br.com.aguaemdia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public final class AvaliacaoDtos {
    private AvaliacaoDtos() {}

    public record AvaliacaoRequest(@NotNull @Min(1) @Max(5) Short nota, @Size(max = 1000) String comentario) {}

    public record AvaliacaoResponse(Long id, Long atendimentoId, Short nota, String comentario, OffsetDateTime criadoEm,
            boolean contatoDeRetornoAberto) {}

    public record AvaliacaoBaixaItem(Long avaliacaoId, Long atendimentoId, Short nota, String comentario,
            OffsetDateTime criadoEm, Long contatoId, String contatoSituacao) {}

    public record ContatoRetornoRequest(@NotBlank String situacao, @Size(max = 2000) String observacao) {}
}
