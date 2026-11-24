package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecuperacaoSolicitacaoDTO(
        @NotBlank(message = "Informe seu e-mail.") @Email(message = "Esse e-mail não parece válido.") String email) {
}
