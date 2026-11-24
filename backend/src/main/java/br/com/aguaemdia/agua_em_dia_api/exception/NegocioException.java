package br.com.aguaemdia.agua_em_dia_api.exception;

import org.springframework.http.HttpStatus;

/** Erro de regra de negócio. A mensagem é mostrada ao cidadão, então deve ser clara e em português. */
public class NegocioException extends RuntimeException {

    private final HttpStatus status;

    public NegocioException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static NegocioException requisicaoInvalida(String mensagem) {
        return new NegocioException(HttpStatus.BAD_REQUEST, mensagem);
    }

    public static NegocioException naoAutenticado(String mensagem) {
        return new NegocioException(HttpStatus.UNAUTHORIZED, mensagem);
    }

    public static NegocioException proibido(String mensagem) {
        return new NegocioException(HttpStatus.FORBIDDEN, mensagem);
    }

    public static NegocioException naoEncontrado(String mensagem) {
        return new NegocioException(HttpStatus.NOT_FOUND, mensagem);
    }

    public static NegocioException conflito(String mensagem) {
        return new NegocioException(HttpStatus.CONFLICT, mensagem);
    }

    public static NegocioException naoProcessavel(String mensagem) {
        return new NegocioException(HttpStatus.valueOf(422), mensagem);
    }

    public static NegocioException indisponivel(String mensagem) {
        return new NegocioException(HttpStatus.SERVICE_UNAVAILABLE, mensagem);
    }
}
