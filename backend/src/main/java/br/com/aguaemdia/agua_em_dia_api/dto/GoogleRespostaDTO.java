package br.com.aguaemdia.agua_em_dia_api.dto;

/** Se a conta já existe, "login" vem preenchido. Senão, cadastroPendente=true e o app pede CPF, celular e aceite dos termos. */
public record GoogleRespostaDTO(boolean cadastroPendente, String nome, String email, LoginRespostaDTO login) {
}
