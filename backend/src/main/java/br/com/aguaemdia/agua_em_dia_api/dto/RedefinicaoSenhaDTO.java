package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RedefinicaoSenhaDTO(
        @NotBlank(message = "Informe seu e-mail.") @Email(message = "Esse e-mail não parece válido.") String email,
        @NotBlank(message = "Digite o código de 6 dígitos.")
        @Pattern(regexp = "^\\d{6}$", message = "O código tem 6 dígitos.") String codigo,
        @NotBlank(message = "Crie a nova senha.")
        @Size(min = 8, max = 100, message = "A senha precisa ter de 8 a 100 caracteres.") String novaSenha) {
}
