package br.com.aguaemdia.agua_em_dia_api.dto;

/**
 * Resposta do pedido de código. A mensagem é sempre a mesma, exista ou não a conta (não revela quem tem cadastro).
 * O campo codigoDemonstracao só vem preenchido no modo demonstração (app.demo=true), para o Gmail simulado do protótipo.
 */
public record RecuperacaoRespostaDTO(String mensagem, String codigoDemonstracao) {
}
