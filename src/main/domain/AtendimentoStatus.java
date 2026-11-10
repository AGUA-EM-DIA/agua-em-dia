package br.com.aguaemdia.domain;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum AtendimentoStatus {
    AGUARDANDO_EQUIPE, ATRIBUIDO, EM_EXECUCAO, AGUARDANDO_VALIDACAO, CONCLUIDO, CANCELADO;

    public static final Set<AtendimentoStatus> ABERTOS =
            EnumSet.of(AGUARDANDO_EQUIPE, ATRIBUIDO, EM_EXECUCAO, AGUARDANDO_VALIDACAO);

    public boolean aberto() { return ABERTOS.contains(this); }

    public static List<String> nomes(Set<AtendimentoStatus> conjunto) {
        return conjunto.stream().map(Enum::name).toList();
    }
}
