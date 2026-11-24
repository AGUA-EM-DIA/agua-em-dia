package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AquariMensagemDTO(
        @NotBlank(message = "Escreva sua pergunta.") @Size(max = 500, message = "A pergunta pode ter até 500 caracteres.") String texto) {
}
