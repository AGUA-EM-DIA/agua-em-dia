package br.com.aguaemdia.service;

import br.com.aguaemdia.config.AppProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.mail.enabled", havingValue = "true")
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;
    private final String remetente;

    public SmtpEmailSender(JavaMailSender mailSender, AppProperties props) {
        this.mailSender = mailSender;
        this.remetente = props.mail().remetente();
    }

    @Override
    public void enviar(String para, String assunto, String corpo) {
        SimpleMailMessage m = new SimpleMailMessage();
        m.setFrom(remetente);
        m.setTo(para);
        m.setSubject(assunto);
        m.setText(corpo);
        mailSender.send(m);
    }
}
