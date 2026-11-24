package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.NotBlank;

/** Token de identidade (ID token) entregue pelo botão "Continuar com Google". */
public record GoogleCredencialDTO(@NotBlank(message = "Credencial do Google ausente.") String credential) {
}
