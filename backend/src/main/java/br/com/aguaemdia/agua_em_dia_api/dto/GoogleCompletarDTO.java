package br.com.aguaemdia.agua_em_dia_api.dto;

import br.com.aguaemdia.agua_em_dia_api.validacao.Cpf;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados que faltam para concluir o cadastro de quem entrou com o Google pela primeira vez. */
public record GoogleCompletarDTO(
        @NotBlank(message = "Credencial do Google ausente.") String credential,
        @NotBlank(message = "Informe seu CPF.") @Cpf(message = "Esse CPF não parece válido.") String cpf,
        @NotBlank(message = "Informe seu celular com DDD.")
        @Size(max = 20) @Pattern(regexp = "^\\D*(\\d\\D*){10,11}$", message = "Celular inválido. Use DDD e número.") String telefone,
        @AssertTrue(message = "É preciso aceitar os termos de uso e a política de privacidade.") boolean aceitouTermos) {
}
