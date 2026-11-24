package br.com.aguaemdia.agua_em_dia_api.dto;

import br.com.aguaemdia.agua_em_dia_api.entity.SituacaoVazamento;
import jakarta.validation.constraints.NotNull;

public record SituacaoDTO(@NotNull(message = "Informe a nova situação.") SituacaoVazamento situacao) {
}
