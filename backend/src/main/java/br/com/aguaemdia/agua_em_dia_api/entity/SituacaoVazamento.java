package br.com.aguaemdia.agua_em_dia_api.entity;

/** Situação do vazamento. A ordem importa: a situação só avança (ABERTO, REPARO, RESOLVIDO). */
public enum SituacaoVazamento {
    ABERTO,
    REPARO,
    RESOLVIDO
}
