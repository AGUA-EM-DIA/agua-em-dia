package br.com.aguaemdia.service;

import br.com.aguaemdia.common.AposCommit;
import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.config.AppProperties;
import br.com.aguaemdia.domain.TokenRecuperacao;
import br.com.aguaemdia.domain.TokenVerificacao;
import br.com.aguaemdia.domain.Usuario;
import br.com.aguaemdia.repository.TokenRecuperacaoRepository;
import br.com.aguaemdia.repository.TokenVerificacaoRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gera tokens de uso único (256 bits), guarda só o hash SHA-256 e envia o link por e-mail depois do commit.
 * O token em si nunca é gravado nem logado (RNF002/RNF010).
 */
@Service
public class TokenEmailService {

    private static final Logger log = LoggerFactory.getLogger(TokenEmailService.class);

    private final TokenVerificacaoRepository verificacoes;
    private final TokenRecuperacaoRepository recuperacoes;
    private final EmailSender emailSender;
    private final ParametroService parametros;
    private final AppProperties props;
    private final Clock clock;

    public TokenEmailService(TokenVerificacaoRepository verificacoes, TokenRecuperacaoRepository recuperacoes,
            EmailSender emailSender, ParametroService parametros, AppProperties props, Clock clock) {
        this.verificacoes = verificacoes;
        this.recuperacoes = recuperacoes;
        this.emailSender = emailSender;
        this.parametros = parametros;
        this.props = props;
        this.clock = clock;
    }

    @Transactional
    public void enviarVerificacao(Usuario u) {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        verificacoes.invalidarPendentes(u.getId(), agora);
        String token = Textos.tokenAleatorio();
        TokenVerificacao t = new TokenVerificacao();
        t.setUsuarioId(u.getId());
        t.setTokenHash(Textos.sha256Hex(token));
        t.setExpiraEm(agora.plusHours(parametros.longo("token.verificacao_horas")));
        verificacoes.save(t);
        String link = props.urlBaseFront() + "/verificar-email?token=" + token;
        enviarDepoisDoCommit(u.getEmail(), "Confirme seu e-mail — Água em Dia",
                "Olá, " + u.getNome() + "!\n\nConfirme seu e-mail para ativar sua conta:\n" + link
                        + "\n\nSe você não criou esta conta, ignore esta mensagem.");
    }

    /** convite = true: conta criada por um administrador; o link serve para definir a primeira senha. */
    @Transactional
    public void enviarRecuperacao(Usuario u, boolean convite) {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        recuperacoes.invalidarPendentes(u.getId(), agora);
        String token = Textos.tokenAleatorio();
        TokenRecuperacao t = new TokenRecuperacao();
        t.setUsuarioId(u.getId());
        t.setTokenHash(Textos.sha256Hex(token));
        t.setExpiraEm(convite ? agora.plusHours(parametros.longo("token.verificacao_horas"))
                              : agora.plusMinutes(parametros.longo("token.recuperacao_minutos")));
        recuperacoes.save(t);
        String link = props.urlBaseFront() + "/redefinir-senha?token=" + token;
        String assunto = convite ? "Defina sua senha — Água em Dia" : "Recuperação de senha — Água em Dia";
        String texto = convite
                ? "Olá, " + u.getNome() + "!\n\nUma conta foi criada para você. Defina sua senha:\n" + link
                : "Recebemos um pedido para redefinir sua senha:\n" + link
                        + "\n\nO link expira em breve e só pode ser usado uma vez. Se não foi você, ignore esta mensagem.";
        enviarDepoisDoCommit(u.getEmail(), assunto, texto);
    }

    private void enviarDepoisDoCommit(String para, String assunto, String corpo) {
        AposCommit.executar(() -> {
            try {
                emailSender.enviar(para, assunto, corpo);
            } catch (RuntimeException e) {
                log.warn("Falha ao enviar e-mail de conta: {}", e.getClass().getSimpleName());
            }
        });
    }
}
