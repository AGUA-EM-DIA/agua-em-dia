package br.com.aguaemdia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Tarefas periódicas. Em múltiplas instâncias, rode só em uma (ou use ShedLock) para não duplicar envios. */
@Component
public class Agendadores {

    private static final Logger log = LoggerFactory.getLogger(Agendadores.class);

    private final NotificacaoService notificacoes;
    private final PrioridadeService prioridade;

    public Agendadores(NotificacaoService notificacoes, PrioridadeService prioridade) {
        this.notificacoes = notificacoes;
        this.prioridade = prioridade;
    }

    @Scheduled(fixedDelayString = "${app.notificacoes.intervalo-ms:30000}", initialDelay = 20000)
    public void enviarEmailsPendentes() {
        try {
            notificacoes.processarFila();
        } catch (RuntimeException e) {
            log.error("Falha ao processar fila de notificações", e);
        }
    }

    /** O fator de atraso (A) cresce com o tempo: recalcula periodicamente as ocorrências ativas. */
    @Scheduled(fixedDelayString = "${app.prioridade.intervalo-ms:900000}", initialDelay = 60000)
    public void recalcularPrioridades() {
        for (Long id : prioridade.idsParaRecalculoPeriodico()) {
            try {
                prioridade.recalcular(id, "recalculo_periodico");
            } catch (RuntimeException e) {
                log.warn("Falha ao recalcular prioridade da ocorrência {}", id);
            }
        }
    }
}
