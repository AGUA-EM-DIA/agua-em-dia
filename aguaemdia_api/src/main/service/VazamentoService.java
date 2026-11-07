package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.domain.Vazamento;
import br.com.aguaemdia.domain.VazamentoStatus;
import br.com.aguaemdia.repository.VazamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Máquina de estados da ocorrência: toda mudança passa por aqui (valida, grava histórico e avisa os cidadãos). */
@Service
public class VazamentoService {

    private final VazamentoRepository vazamentos;
    private final AuditoriaService auditoria;
    private final NotificacaoService notificacoes;

    public VazamentoService(VazamentoRepository vazamentos, AuditoriaService auditoria, NotificacaoService notificacoes) {
        this.vazamentos = vazamentos;
        this.auditoria = auditoria;
        this.notificacoes = notificacoes;
    }

    @Transactional(readOnly = true)
    public Vazamento obter(Long id) {
        return vazamentos.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Ocorrência não encontrada."));
    }

    /** Obtém com trava de linha. Dentro da mesma transação, trave sempre em ordem crescente de id. */
    @Transactional
    public Vazamento obterParaAtualizar(Long id) {
        return vazamentos.findByIdParaAtualizar(id).orElseThrow(() -> ApiException.naoEncontrado("Ocorrência não encontrada."));
    }

    @Transactional
    public void mudarStatus(Vazamento v, VazamentoStatus novo, Long ator, String justificativa) {
        VazamentoStatus atual = VazamentoStatus.valueOf(v.getStatus());
        if (!atual.podeIrPara(novo)) {
            throw ApiException.negocio("Transição de status não permitida: " + atual + " → " + novo + ".");
        }
        aplicar(v, atual, novo, ator, justificativa);
    }

    /** Fluxo excepcional (RF034): desfaz a duplicidade e devolve a ocorrência para análise. */
    @Transactional
    public void reverterDuplicada(Vazamento v, Long ator, String justificativa) {
        VazamentoStatus atual = VazamentoStatus.valueOf(v.getStatus());
        if (atual != VazamentoStatus.DUPLICADA) throw ApiException.negocio("A ocorrência não está marcada como duplicada.");
        aplicar(v, atual, VazamentoStatus.EM_ANALISE, ator, justificativa);
    }

    private void aplicar(Vazamento v, VazamentoStatus atual, VazamentoStatus novo, Long ator, String justificativa) {
        v.setStatus(novo.name());
        vazamentos.save(v);
        auditoria.statusAlterado("VAZAMENTO", v.getId(), atual.name(), novo.name(), ator, justificativa);
        avisarCidadaos(v, novo);
    }

    private void avisarCidadaos(Vazamento v, VazamentoStatus novo) {
        String titulo;
        String mensagem;
        switch (novo) {
            case CONFIRMADA -> { titulo = "Ocorrência confirmada"; mensagem = "Seu relato foi confirmado pela central e será encaminhado para atendimento."; }
            case EM_ATENDIMENTO -> { titulo = "Equipe em atendimento"; mensagem = "Uma equipe iniciou o atendimento da ocorrência que você relatou."; }
            case RESOLVIDA -> { titulo = "Reparo concluído"; mensagem = "O reparo foi concluído e validado. Você pode avaliar o atendimento no aplicativo."; }
            case REABERTA -> { titulo = "Ocorrência reaberta"; mensagem = "A ocorrência foi reaberta e voltará para atendimento."; }
            case CANCELADA -> { titulo = "Ocorrência cancelada"; mensagem = "A ocorrência relacionada ao seu relato foi cancelada pela central."; }
            case DUPLICADA -> { titulo = "Relato agrupado"; mensagem = "Seu relato foi agrupado a uma ocorrência já registrada para o mesmo problema."; }
            default -> { return; }
        }
        notificacoes.notificarAutores(v.getId(), "STATUS_" + novo.name(), titulo, mensagem);
    }
}
