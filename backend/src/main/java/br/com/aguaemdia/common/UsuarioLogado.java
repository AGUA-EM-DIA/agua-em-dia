package br.com.aguaemdia.common;

import java.util.Set;

/** Principal da requisição autenticada (montado pelo JwtAuthFilter a cada chamada). */
public record UsuarioLogado(Long id, String email, Set<String> papeis) {

    public boolean temPapel(String papel) {
        return papeis.contains(papel);
    }

    public boolean ehOperacao() {
        return papeis.contains(Papeis.OPERADOR) || papeis.contains(Papeis.GESTOR) || papeis.contains(Papeis.ADMINISTRADOR);
    }
}
