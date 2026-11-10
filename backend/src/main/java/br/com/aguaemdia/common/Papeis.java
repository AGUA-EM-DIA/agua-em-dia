package br.com.aguaemdia.common;

/** Nomes dos papéis (tabela Papel) e expressões usadas em @PreAuthorize. */
public final class Papeis {
    private Papeis() {}

    public static final String CIDADAO = "CIDADAO";
    public static final String EQUIPE = "EQUIPE";
    public static final String OPERADOR = "OPERADOR";
    public static final String GESTOR = "GESTOR";
    public static final String ADMINISTRADOR = "ADMINISTRADOR";

    public static final String E_CIDADAO = "hasRole('CIDADAO')";
    public static final String E_EQUIPE = "hasRole('EQUIPE')";
    public static final String E_OPERACAO = "hasAnyRole('OPERADOR','GESTOR','ADMINISTRADOR')";
    public static final String E_GESTAO = "hasAnyRole('GESTOR','ADMINISTRADOR')";
    public static final String E_ADMIN = "hasRole('ADMINISTRADOR')";
}
