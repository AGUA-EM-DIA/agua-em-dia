package br.com.aguaemdia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Somente desenvolvimento: escreve o e-mail no log (inclui o link com token, para você testar o fluxo).
 * Em produção use app.mail.enabled=true — o RNF010 proíbe token em log.
 */
@Component
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "false", matchIfMissing = true)
public class ConsoleEmailSender implements EmailSender {

    private static final Logger log = LoggerFactory.getLogger(ConsoleEmailSender.class);

    @Override
    public void enviar(String para, String assunto, String corpo) {
        log.info("[E-MAIL SIMULADO] para={} assunto={}\n{}", para, assunto, corpo);
    }
}
