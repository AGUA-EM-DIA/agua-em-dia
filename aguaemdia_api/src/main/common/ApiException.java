package br.com.aguaemdia.common;

import org.springframework.http.HttpStatus;

/** Erro de API com status HTTP e código estável para o front tratar. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String codigo;

    private ApiException(HttpStatus status, String codigo, String mensagem) {
        super(mensagem);
        this.status = status;
        this.codigo = codigo;
    }

    public HttpStatus getStatus() { return status; }
    public String getCodigo() { return codigo; }

    public static ApiException invalido(String m)        { return new ApiException(HttpStatus.BAD_REQUEST, "REQUISICAO_INVALIDA", m); }
    public static ApiException naoAutenticado(String m)  { return new ApiException(HttpStatus.UNAUTHORIZED, "NAO_AUTENTICADO", m); }
    public static ApiException proibido(String m)        { return new ApiException(HttpStatus.FORBIDDEN, "ACESSO_NEGADO", m); }
    public static ApiException naoEncontrado(String m)   { return new ApiException(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", m); }
    public static ApiException conflito(String m)        { return new ApiException(HttpStatus.CONFLICT, "CONFLITO", m); }
    public static ApiException negocio(String m)         { return new ApiException(HttpStatus.UNPROCESSABLE_ENTITY, "REGRA_DE_NEGOCIO", m); }
}
