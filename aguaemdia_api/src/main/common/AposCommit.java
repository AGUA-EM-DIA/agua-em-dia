package br.com.aguaemdia.common;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public final class AposCommit {
    private AposCommit() {}

    /** Executa depois do commit (ex.: enviar e-mail só se a transação deu certo). */
    public static void executar(Runnable acao) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() { acao.run(); }
            });
        } else {
            acao.run();
        }
    }

    /** Executa se a transação for desfeita (ex.: apagar arquivo gravado em disco). */
    public static void aoDesfazer(Runnable acao) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status == STATUS_ROLLED_BACK) acao.run();
                }
            });
        }
    }
}
