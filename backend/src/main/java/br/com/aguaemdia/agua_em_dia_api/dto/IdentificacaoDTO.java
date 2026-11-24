package br.com.aguaemdia.agua_em_dia_api.dto;

/** Resultado da identificação do vazamento pela foto. Tipo "NENHUM" significa que a foto não mostra vazamento. */
public record IdentificacaoDTO(String tipo, String porte, double confianca, String motivo) {
}
