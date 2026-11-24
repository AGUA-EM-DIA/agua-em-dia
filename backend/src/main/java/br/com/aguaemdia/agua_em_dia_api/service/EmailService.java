package br.com.aguaemdia.agua_em_dia_api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envia e-mails. Só funciona quando o SMTP está configurado (spring.mail.host e credenciais).
 * Sem SMTP, o envio é ignorado e o aplicativo continua funcionando.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> remetente;
    private final String de;

    public EmailService(ObjectProvider<JavaMailSender> remetente, @Value("${app.mail.remetente:}") String de) {
        this.remetente = remetente;
        this.de = de;
    }

    /** Devolve true se o e-mail foi entregue ao servidor SMTP. */
    public boolean enviarCodigoRecuperacao(String para, String nome, String codigo) {
        JavaMailSender sender = remetente.getIfAvailable();
        if (sender == null) {
            log.info("SMTP não configurado: o e-mail de recuperação de senha não foi enviado.");
            return false;
        }
        try {
            SimpleMailMessage mensagem = new SimpleMailMessage();
            if (!de.isBlank()) {
                mensagem.setFrom(de);
            }
            mensagem.setTo(para);
            mensagem.setSubject("Seu código de recuperação do Água em Dia");
            mensagem.setText("Olá, " + nome + "!\n\n"
                    + "Seu código para criar uma nova senha é: " + codigo + "\n\n"
                    + "Ele vale por 10 minutos. Se você não pediu isso, ignore este e-mail: sua senha continua a mesma.\n\n"
                    + "Água em Dia");
            sender.send(mensagem);
            return true;
        } catch (MailException e) {
            log.warn("Não consegui enviar o e-mail de recuperação: {}", e.getMessage());
            return false;
        }
    }
}
