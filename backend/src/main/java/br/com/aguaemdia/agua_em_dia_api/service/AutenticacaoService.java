package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.LoginDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.LoginRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.UsuarioRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import br.com.aguaemdia.agua_em_dia_api.repository.UsuarioRepository;
import br.com.aguaemdia.agua_em_dia_api.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticacaoService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** Login por e-mail e senha. A mensagem de erro é a mesma para e-mail inexistente e senha errada. */
    @Transactional(readOnly = true)
    public LoginRespostaDTO login(LoginDTO dados) {
        String email = dados.getEmail().trim().toLowerCase();
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null || !passwordEncoder.matches(dados.getSenha(), usuario.getSenha())) {
            throw NegocioException.naoAutenticado("E-mail ou senha incorretos.");
        }
        return respostaDeLogin(usuario);
    }

    /** Monta a resposta com token e dados do cidadão. Também usada pelo login com Google. */
    public LoginRespostaDTO respostaDeLogin(Usuario usuario) {
        Jwt token = jwtService.gerar(usuario);
        return new LoginRespostaDTO(token.getTokenValue(), token.getExpiresAt().toEpochMilli(), UsuarioRespostaDTO.de(usuario));
    }
}
