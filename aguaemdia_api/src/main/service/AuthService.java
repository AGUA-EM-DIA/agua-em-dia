package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.common.Papeis;
import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.config.JwtService;
import br.com.aguaemdia.domain.TokenRecuperacao;
import br.com.aguaemdia.domain.TokenVerificacao;
import br.com.aguaemdia.domain.Usuario;
import br.com.aguaemdia.dto.AuthDtos.LoginRequest;
import br.com.aguaemdia.dto.AuthDtos.LoginResponse;
import br.com.aguaemdia.dto.AuthDtos.RegistroRequest;
import br.com.aguaemdia.repository.TokenRecuperacaoRepository;
import br.com.aguaemdia.repository.TokenVerificacaoRepository;
import br.com.aguaemdia.repository.UsuarioRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final String MSG_CREDENCIAIS = "E-mail ou senha inválidos.";
    private static final String MSG_TOKEN = "Link inválido, expirado ou já utilizado.";

    private final UsuarioRepository usuarios;
    private final TokenVerificacaoRepository tokensVerificacao;
    private final TokenRecuperacaoRepository tokensRecuperacao;
    private final UsuarioService usuarioService;
    private final TokenEmailService tokenEmail;
    private final AuditoriaService auditoria;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final Clock clock;
    private final String hashFalso;

    public AuthService(UsuarioRepository usuarios, TokenVerificacaoRepository tokensVerificacao,
            TokenRecuperacaoRepository tokensRecuperacao, UsuarioService usuarioService, TokenEmailService tokenEmail,
            AuditoriaService auditoria, PasswordEncoder encoder, JwtService jwt, Clock clock) {
        this.usuarios = usuarios;
        this.tokensVerificacao = tokensVerificacao;
        this.tokensRecuperacao = tokensRecuperacao;
        this.usuarioService = usuarioService;
        this.tokenEmail = tokenEmail;
        this.auditoria = auditoria;
        this.encoder = encoder;
        this.jwt = jwt;
        this.clock = clock;
        this.hashFalso = encoder.encode("hash-falso-para-igualar-o-tempo-de-resposta");
    }

    /** RF001: cadastro de cidadão. A conta só fica ativa depois da confirmação do e-mail. */
    @Transactional
    public void registrar(RegistroRequest r) {
        Usuario u = usuarioService.criar(r.nome(), r.email(), r.telefone(), r.senha(),
                "PENDENTE_VERIFICACAO", List.of(Papeis.CIDADAO), null);
        tokenEmail.enviarVerificacao(u);
    }

    @Transactional
    public void verificarEmail(String token) {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        TokenVerificacao t = tokensVerificacao.findByTokenHash(Textos.sha256Hex(token))
                .filter(x -> x.getUtilizadoEm() == null && x.getExpiraEm().isAfter(agora))
                .orElseThrow(() -> ApiException.invalido(MSG_TOKEN));
        Usuario u = usuarioService.obter(t.getUsuarioId());
        if ("DESATIVADO".equals(u.getSituacao()) || "ANONIMIZADO".equals(u.getSituacao())) throw ApiException.invalido(MSG_TOKEN);
        t.setUtilizadoEm(agora);
        u.setSituacao("ATIVO");
        u.setEmailVerificadoEm(agora);
    }

    /** Resposta idêntica exista ou não a conta (não revela cadastros). */
    @Transactional
    public void reenviarVerificacao(String email) {
        usuarios.findByEmailIgnoreCase(UsuarioService.normalizarEmail(email))
                .filter(u -> "PENDENTE_VERIFICACAO".equals(u.getSituacao()))
                .ifPresent(tokenEmail::enviarVerificacao);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest r) {
        Usuario u = usuarios.findByEmailIgnoreCase(UsuarioService.normalizarEmail(r.email())).orElse(null);
        boolean senhaOk = encoder.matches(r.senha(), u != null ? u.getSenhaHash() : hashFalso);   // custo igual p/ conta inexistente
        if (u == null || !senhaOk) throw ApiException.naoAutenticado(MSG_CREDENCIAIS);
        switch (u.getSituacao()) {
            case "ATIVO" -> { }
            case "PENDENTE_VERIFICACAO" -> throw ApiException.proibido("Confirme seu e-mail antes de entrar.");
            default -> throw ApiException.naoAutenticado(MSG_CREDENCIAIS);
        }
        Instant agora = clock.instant();
        String token = jwt.gerar(u.getId(), agora);
        return new LoginResponse(token, "Bearer", OffsetDateTime.ofInstant(jwt.expiraEm(agora), ZoneOffset.UTC),
                usuarioService.resumo(u.getId()));
    }

    /** RF002: sempre responde igual; só envia e-mail se a conta existir e estiver utilizável. */
    @Transactional
    public void esqueciSenha(String email) {
        usuarios.findByEmailIgnoreCase(UsuarioService.normalizarEmail(email))
                .filter(u -> "ATIVO".equals(u.getSituacao()) || "PENDENTE_VERIFICACAO".equals(u.getSituacao()))
                .ifPresent(u -> tokenEmail.enviarRecuperacao(u, false));
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        TokenRecuperacao t = tokensRecuperacao.findByTokenHash(Textos.sha256Hex(token))
                .filter(x -> x.getUtilizadoEm() == null && x.getExpiraEm().isAfter(agora))
                .orElseThrow(() -> ApiException.invalido(MSG_TOKEN));
        Usuario u = usuarioService.obter(t.getUsuarioId());
        if (!"ATIVO".equals(u.getSituacao()) && !"PENDENTE_VERIFICACAO".equals(u.getSituacao())) throw ApiException.invalido(MSG_TOKEN);
        u.setSenhaHash(encoder.encode(novaSenha));
        if ("PENDENTE_VERIFICACAO".equals(u.getSituacao())) {     // receber o link prova a posse do e-mail
            u.setSituacao("ATIVO");
            u.setEmailVerificadoEm(agora);
        }
        t.setUtilizadoEm(agora);
        tokensRecuperacao.invalidarPendentes(u.getId(), agora);
        auditoria.registrar(u.getId(), "SENHA_REDEFINIDA", "Usuario", u.getId(), null, null);
    }
}
