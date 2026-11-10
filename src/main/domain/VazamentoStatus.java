package br.com.aguaemdia.domain;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Ciclo de vida da ocorrência (documentação 5.1 + AGUARDANDO_VALIDACAO do RF042). */
public enum VazamentoStatus {
    PROVISORIA, EM_ANALISE, CONFIRMADA, AGUARDANDO_ATENDIMENTO, EM_ATENDIMENTO,
    AGUARDANDO_VALIDACAO, RESOLVIDA, REABERTA, DUPLICADA, CANCELADA;

    private static final Map<VazamentoStatus, Set<VazamentoStatus>> TRANSICOES = new EnumMap<>(VazamentoStatus.class);

    static {
        TRANSICOES.put(PROVISORIA, EnumSet.of(EM_ANALISE, DUPLICADA, CANCELADA));
        TRANSICOES.put(EM_ANALISE, EnumSet.of(CONFIRMADA, DUPLICADA, CANCELADA));
        TRANSICOES.put(CONFIRMADA, EnumSet.of(AGUARDANDO_ATENDIMENTO, CANCELADA));
        TRANSICOES.put(AGUARDANDO_ATENDIMENTO, EnumSet.of(EM_ATENDIMENTO, CANCELADA));
        TRANSICOES.put(EM_ATENDIMENTO, EnumSet.of(AGUARDANDO_VALIDACAO, AGUARDANDO_ATENDIMENTO, CANCELADA));
        TRANSICOES.put(AGUARDANDO_VALIDACAO, EnumSet.of(RESOLVIDA, EM_ATENDIMENTO, CANCELADA));
        TRANSICOES.put(RESOLVIDA, EnumSet.of(REABERTA));
        TRANSICOES.put(REABERTA, EnumSet.of(AGUARDANDO_ATENDIMENTO, EM_ATENDIMENTO, CANCELADA));
        // DUPLICADA e CANCELADA: sem transição normal (reversão de duplicidade é fluxo excepcional auditado).
    }

    /** Ainda não confirmadas pelo operador. */
    public static final Set<VazamentoStatus> PROVISORIOS = EnumSet.of(PROVISORIA, EM_ANALISE);
    /** Vazamentos canônicos operacionalmente abertos. */
    public static final Set<VazamentoStatus> ABERTOS_CONFIRMADOS =
            EnumSet.of(CONFIRMADA, AGUARDANDO_ATENDIMENTO, EM_ATENDIMENTO, AGUARDANDO_VALIDACAO, REABERTA);
    /** Provisórios + confirmados abertos. */
    public static final Set<VazamentoStatus> ATIVOS = EnumSet.of(PROVISORIA, EM_ANALISE, CONFIRMADA,
            AGUARDANDO_ATENDIMENTO, EM_ATENDIMENTO, AGUARDANDO_VALIDACAO, REABERTA);

    public boolean podeIrPara(VazamentoStatus novo) {
        return TRANSICOES.getOrDefault(this, EnumSet.noneOf(VazamentoStatus.class)).contains(novo);
    }

    public boolean cancelavel() { return podeIrPara(CANCELADA); }

    /** Texto amigável para mensagens ao cidadão. */
    public String rotulo() {
        return switch (this) {
            case PROVISORIA -> "Recebido";
            case EM_ANALISE -> "Em análise";
            case CONFIRMADA -> "Confirmado";
            case AGUARDANDO_ATENDIMENTO -> "Aguardando atendimento";
            case EM_ATENDIMENTO -> "Em atendimento";
            case AGUARDANDO_VALIDACAO -> "Reparo em validação";
            case RESOLVIDA -> "Resolvido";
            case REABERTA -> "Reaberto";
            case DUPLICADA -> "Agrupado a outra ocorrência";
            case CANCELADA -> "Cancelado";
        };
    }

    public static List<String> nomes(Set<VazamentoStatus> conjunto) {
        return conjunto.stream().map(Enum::name).toList();
    }
}
