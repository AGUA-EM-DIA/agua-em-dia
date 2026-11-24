package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.RecuperacaoRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.RedefinicaoSenhaDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.RecuperacaoSenha;
import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import br.com.aguaemdia.agua_em_dia_api.repository.RecuperacaoSenhaRepository;
import br.com.aguaemdia.agua_em_dia_api.repository.UsuarioRepository;
import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/** Recuperação de senha por código de 6 dígitos enviado por e-mail (validade de 10 minutos, até 5 tentativas). */
@Service
public class RecuperacaoSenhaService {

    private static final int MINUTOS_DE_VALIDADE = 10;
    private static final int MAXIMO_DE_TENTATIVAS = 5;
    private static final int SEGUNDOS_ENTRE_PEDIDOS = 15;
    private static final String MENSAGEM_PADRAO = "Se esse e-mail tiver cadastro, enviamos um código de 6 dígitos.";

    private final UsuarioRepository usuarios;
    private final RecuperacaoSenhaRepository codigos;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final boolean demo;
    private final SecureRandom aleatorio = new SecureRandom();

    public RecuperacaoSenhaService(UsuarioRepository usuarios, RecuperacaoSenhaRepository codigos,
                                   PasswordEncoder passwordEncoder, EmailService emailService,
                                   @Value("${app.demo:false}") boolean demo) {
        this.usuarios = usuarios;
        this.codigos = codigos;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.demo = demo;
    }

    /**
     * Gera e envia o código. A resposta é a mesma exista ou não a conta, para não revelar quem tem cadastro.
     * No modo demonstração o código também volta na resposta, para aparecer no Gmail simulado do celular.
     */
    @Transactional
    public RecuperacaoRespostaDTO solicitar(String emailInformado) {
        Optional<Usuario> encontrado = usuarios.findByEmail(emailInformado.trim().toLowerCase());
        if (encontrado.isEmpty()) {
            return new RecuperacaoRespostaDTO(MENSAGEM_PADRAO, null);
        }
        Usuario usuario = encontrado.get();

        Optional<RecuperacaoSenha> ultimo = codigos.findFirstByUsuarioIdAndUsadoEmIsNullOrderByCriadoEmDesc(usuario.getId());
        if (ultimo.isPresent() && ultimo.get().getCriadoEm().isAfter(Tempo.agora().minusSeconds(SEGUNDOS_ENTRE_PEDIDOS))) {
            throw new NegocioException(HttpStatus.TOO_MANY_REQUESTS, "Aguarde alguns segundos para pedir outro código.");
        }

        // Um código novo cancela os anteriores
        LocalDateTime agora = Tempo.agora();
        for (RecuperacaoSenha antigo : codigos.findByUsuarioIdAndUsadoEmIsNull(usuario.getId())) {
            antigo.setUsadoEm(agora);
        }

        String codigo = String.valueOf(100000 + aleatorio.nextInt(900000));
        codigos.save(new RecuperacaoSenha(usuario, passwordEncoder.encode(codigo), agora.plusMinutes(MINUTOS_DE_VALIDADE)));
        emailService.enviarCodigoRecuperacao(usuario.getEmail(), usuario.getNome(), codigo);

        return new RecuperacaoRespostaDTO(MENSAGEM_PADRAO, demo ? codigo : null);
    }

    /** Confere o código e grava a nova senha. Erros de código contam tentativa, mesmo falhando (noRollbackFor). */
    @Transactional(noRollbackFor = NegocioException.class)
    public void redefinir(RedefinicaoSenhaDTO dados) {
        Usuario usuario = usuarios.findByEmail(dados.email().trim().toLowerCase())
                .orElseThrow(() -> NegocioException.requisicaoInvalida("Código inválido ou expirado. Peça um novo."));
        RecuperacaoSenha pedido = codigos.findFirstByUsuarioIdAndUsadoEmIsNullOrderByCriadoEmDesc(usuario.getId())
                .orElseThrow(() -> NegocioException.requisicaoInvalida("Código inválido ou expirado. Peça um novo."));

        if (pedido.getExpiraEm().isBefore(Tempo.agora())) {
            throw NegocioException.requisicaoInvalida("O código expirou. Peça um novo.");
        }
        if (pedido.getTentativas() >= MAXIMO_DE_TENTATIVAS) {
            throw NegocioException.requisicaoInvalida("Muitas tentativas. Peça um novo código.");
        }
        if (!passwordEncoder.matches(dados.codigo(), pedido.getCodigoHash())) {
            pedido.setTentativas(pedido.getTentativas() + 1);
            codigos.save(pedido);
            int restantes = MAXIMO_DE_TENTATIVAS - pedido.getTentativas();
            throw NegocioException.requisicaoInvalida(restantes > 0
                    ? "Código incorreto. Restam " + restantes + " tentativas."
                    : "Muitas tentativas. Peça um novo código.");
        }

        usuario.setSenha(passwordEncoder.encode(dados.novaSenha()));
        usuarios.save(usuario);
        pedido.setUsadoEm(Tempo.agora());
        codigos.save(pedido);
    }
}
