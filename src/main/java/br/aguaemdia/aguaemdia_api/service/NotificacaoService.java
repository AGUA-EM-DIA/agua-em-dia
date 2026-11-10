package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.config.AppProperties;
import br.com.aguaemdia.domain.EnvioNotificacao;
import br.com.aguaemdia.domain.Notificacao;
import br.com.aguaemdia.domain.Usuario;
import br.com.aguaemdia.repository.EnvioNotificacaoRepository;
import br.com.aguaemdia.repository.NotificacaoRepository;
import br.com.aguaemdia.repository.RelatoRepository;
import br.com.aguaemdia.repository.UsuarioRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Notificações no app + e-mail por "outbox": o e-mail é gravado como PENDENTE na mesma transação do fato
 * gerador e enviado depois, com retentativas (RF025). Assim, e-mail fora do ar não derruba a operação.
 */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository notificacoes;
    private final EnvioNotificacaoRepository envios;
    private final RelatoRepository relatos;
    private final UsuarioRepository usuarios;
    private final EmailSender emailSender;
    private final ParametroService parametros;
    private final AppProperties props;
    private final Clock clock;

    public NotificacaoService(NotificacaoRepository notificacoes, EnvioNotificacaoRepository envios,
            RelatoRepository relatos, UsuarioRepository usuarios, EmailSender emailSender,
            ParametroService parametros, AppProperties props, Clock clock) {
        this.notificacoes = notificacoes;
        this.envios = envios;
        this.relatos = relatos;
        this.usuarios = usuarios;
        this.emailSender = emailSender;
        this.parametros = parametros;
        this.props = props;
        this.clock = clock;
    }

    /** Avisa todos os cidadãos que relataram o vazamento. */
    @Transactional
    public void notificarAutores(Long vazamentoId, String tipo, String titulo, String mensagem) {
        for (Long usuarioId : relatos.autoresDistintos(vazamentoId)) {
            notificarUsuario(usuarioId, vazamentoId, tipo, titulo, mensagem);
        }
    }

    @Transactional
    public void notificarUsuario(Long usuarioId, Long vazamentoId, String tipo, String titulo, String mensagem) {
        Notificacao n = new Notificacao();
        n.setUsuarioId(usuarioId);
        n.setVazamentoId(vazamentoId);
        n.setTipo(tipo);
        n.setTitulo(Textos.truncar(titulo, 150));
        n.setMensagem(Textos.truncar(mensagem, 1000));
        notificacoes.save(n);

        OffsetDateTime agora = OffsetDateTime.now(clock);
        EnvioNotificacao app = new EnvioNotificacao();
        app.setNotificacaoId(n.getId());
        app.setCanal("APP");
        app.setSituacao("ENVIADO");
        app.setEnviadoEm(agora);
        envios.save(app);

        EnvioNotificacao email = new EnvioNotificacao();
        email.setNotificacaoId(n.getId());
        email.setCanal("EMAIL");
        email.setSituacao("PENDENTE");
        envios.save(email);
    }

    /** Chamado pelo agendador: envia e-mails pendentes (lote de 50) com recuo exponencial. */
    @Transactional
    public void processarFila() {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        int maxTentativas = parametros.inteiro("notificacao.maximo_tentativas");
        for (EnvioNotificacao e : envios.pendentes(agora, PageRequest.of(0, 50))) {
            Notificacao n = notificacoes.findById(e.getNotificacaoId()).orElse(null);
            Usuario u = n == null ? null : usuarios.findById(n.getUsuarioId()).orElse(null);
            if (u == null || !"ATIVO".equals(u.getSituacao())) {
                e.setSituacao("FALHA");
                e.setErro("Destinatário indisponível");
                continue;
            }
            try {
                String corpo = n.getMensagem() + "\n\nAcesse o aplicativo: " + props.urlBaseFront();
                emailSender.enviar(u.getEmail(), n.getTitulo(), corpo);
                e.setSituacao("ENVIADO");
                e.setEnviadoEm(agora);
                e.setErro(null);
            } catch (RuntimeException ex) {
                int tentativas = e.getTentativas() + 1;
                e.setTentativas(tentativas);
                e.setErro(Textos.truncar(ex.getClass().getSimpleName(), 500));   // sem detalhes: podem conter dados pessoais
                if (tentativas >= maxTentativas) {
                    e.setSituacao("FALHA");
                } else {
                    e.setProximaTentativaEm(agora.plusMinutes(1L << tentativas));
                }
                log.warn("Falha ao enviar notificação {} (tentativa {})", e.getId(), tentativas);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<Notificacao> listar(Long usuarioId, Pageable pageable) {
        return notificacoes.findByUsuarioIdOrderByCriadoEmDesc(usuarioId, pageable);
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long usuarioId) {
        return notificacoes.countByUsuarioIdAndLidaEmIsNull(usuarioId);
    }

    @Transactional
    public void marcarLida(Long usuarioId, Long id) {
        Notificacao n = notificacoes.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> ApiException.naoEncontrado("Notificação não encontrada."));
        if (n.getLidaEm() == null) n.setLidaEm(OffsetDateTime.now(clock));
    }
}
