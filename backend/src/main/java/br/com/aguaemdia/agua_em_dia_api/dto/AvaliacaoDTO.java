package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoDTO(
        @NotNull(message = "Dê uma nota de 1 a 5.")
        @Min(value = 1, message = "A nota vai de 1 a 5.")
        @Max(value = 5, message = "A nota vai de 1 a 5.") Integer nota,
        @Size(max = 500, message = "O comentário pode ter até 500 caracteres.") String comentario) {
}
