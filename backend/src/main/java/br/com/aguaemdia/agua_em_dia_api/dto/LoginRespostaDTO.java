package br.com.aguaemdia.agua_em_dia_api.dto;

/** Resposta do login: o token JWT (enviar no cabeçalho Authorization: Bearer ...), a validade e os dados do cidadão. */
public record LoginRespostaDTO(String token, long expiraEm, UsuarioRespostaDTO usuario) {
}
