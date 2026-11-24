package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.Size;

public record VazamentoDescricaoDTO(
        @Size(max = 1000, message = "A descrição pode ter até 1000 caracteres.") String descricao) {
}
