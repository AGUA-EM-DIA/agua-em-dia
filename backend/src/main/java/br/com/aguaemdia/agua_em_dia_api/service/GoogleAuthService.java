package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.GoogleCompletarDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.GoogleRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.LoginRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import br.com.aguaemdia.agua_em_dia_api.repository.UsuarioRepository;
import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import br.com.aguaemdia.agua_em_dia_api.validacao.CpfValidador;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Login e cadastro com Google. Só fica ativo com o ID de cliente configurado (GOOGLE_CLIENT_ID).
 * O servidor confere a assinatura, o emissor, o público e a validade do token entregue pelo Google.
 */
@Service
public class GoogleAuthService {

    private static final String CHAVES_PUBLICAS_GOOGLE = "https://www.googleapis.com/oauth2/v3/certs";

    private final String clientId;
    private final UsuarioRepository usuarios;
    private final PasswordEncoder passwordEncoder;
    private final AutenticacaoService autenticacao;
    private NimbusJwtDecoder decodificador;

    public GoogleAuthService(@Value("${app.google.client-id:}") String clientId, UsuarioRepository usuarios,
                             PasswordEncoder passwordEncoder, AutenticacaoService autenticacao) {
        this.clientId = clientId;
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
        this.autenticacao = autenticacao;
    }

    public boolean ativo() {
        return !clientId.isBlank();
    }

    /** Se a conta já existe, entra. Se não, avisa o app para pedir CPF, celular e aceite dos termos. */
    @Transactional(readOnly = true)
    public GoogleRespostaDTO entrar(String credencial) {
        Jwt google = verificar(credencial);
        String email = emailDe(google);
        return usuarios.findByEmail(email)
                .map(u -> new GoogleRespostaDTO(false, u.getNome(), email, autenticacao.respostaDeLogin(u)))
                .orElseGet(() -> new GoogleRespostaDTO(true, nomeDe(google, email), email, null));
    }

    /** Conclui o cadastro de quem entrou com o Google pela primeira vez. */
    @Transactional
    public LoginRespostaDTO completar(GoogleCompletarDTO dados) {
        Jwt google = verificar(dados.credential());
        String email = emailDe(google);
        String cpf = CpfValidador.apenasDigitos(dados.cpf());
        if (usuarios.existsByEmail(email)) {
            throw NegocioException.conflito("Já existe uma conta com esse e-mail. Entre com o Google novamente.");
        }
        if (usuarios.existsByCpf(cpf)) {
            throw NegocioException.conflito("Esse CPF já está cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nomeDe(google, email));
        usuario.setNomeUsuario(nomeDeUsuarioLivre(email));
        usuario.setCpf(cpf);
        usuario.setEmail(email);
        usuario.setTelefone(CpfValidador.apenasDigitos(dados.telefone()));
        // Conta do Google não tem senha própria; a senha aleatória impede entrar por e-mail e senha
        usuario.setSenha(passwordEncoder.encode(UUID.randomUUID().toString()));
        usuario.setTermosAceitosEm(Tempo.agora());
        return autenticacao.respostaDeLogin(usuarios.save(usuario));
    }

    private Jwt verificar(String credencial) {
        if (!ativo()) {
            throw NegocioException.indisponivel("O login com Google ainda não foi ativado neste servidor.");
        }
        try {
            Jwt token = decodificador().decode(credencial);
            if (!Boolean.TRUE.equals(token.getClaimAsBoolean("email_verified")) || emailDe(token).isBlank()) {
                throw NegocioException.naoAutenticado("O Google não confirmou o e-mail dessa conta.");
            }
            return token;
        } catch (JwtException e) {
            throw NegocioException.naoAutenticado("Não consegui confirmar o login com o Google. Tente de novo.");
        }
    }

    private NimbusJwtDecoder decodificador() {
        if (decodificador == null) {
            NimbusJwtDecoder novo = NimbusJwtDecoder.withJwkSetUri(CHAVES_PUBLICAS_GOOGLE).build();
            OAuth2TokenValidator<Jwt> emissorEPublico = token -> {
                String emissor = token.getClaimAsString("iss");
                boolean emissorOk = "https://accounts.google.com".equals(emissor) || "accounts.google.com".equals(emissor);
                boolean publicoOk = token.getAudience() != null && token.getAudience().contains(clientId);
                return emissorOk && publicoOk
                        ? OAuth2TokenValidatorResult.success()
                        : OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "Token do Google inválido.", null));
            };
            novo.setJwtValidator(new DelegatingOAuth2TokenValidator<>(new JwtTimestampValidator(), emissorEPublico));
            decodificador = novo;
        }
        return decodificador;
    }

    private static String emailDe(Jwt google) {
        String email = google.getClaimAsString("email");
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static String nomeDe(Jwt google, String email) {
        String nome = google.getClaimAsString("name");
        return (nome == null || nome.isBlank()) ? email.substring(0, email.indexOf('@')) : nome.trim();
    }

    /** Cria um nome de usuário a partir do e-mail e acrescenta números até achar um livre. */
    private String nomeDeUsuarioLivre(String email) {
        String base = email.substring(0, email.indexOf('@')).toLowerCase().replaceAll("[^a-z0-9._]", "");
        if (base.length() < 3) {
            base = base + "usr";
        }
        base = base.substring(0, Math.min(base.length(), 16));
        String candidato = base;
        int numero = 1;
        while (usuarios.existsByNomeUsuario(candidato)) {
            candidato = base + numero++;
        }
        return candidato;
    }
}
