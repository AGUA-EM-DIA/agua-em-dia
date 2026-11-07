package br.com.aguaemdia.config;

import br.com.aguaemdia.common.Papeis;
import br.com.aguaemdia.repository.UsuarioPapelRepository;
import br.com.aguaemdia.repository.UsuarioRepository;
import br.com.aguaemdia.service.UsuarioService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Cria o PRIMEIRO administrador a partir de ADMIN_EMAIL / ADMIN_SENHA (variáveis de ambiente), somente se ainda
 * não existir nenhum. Depois do primeiro acesso, remova as variáveis.
 */
@Component
public class BootstrapAdmin implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdmin.class);

    private final AppProperties props;
    private final UsuarioRepository usuarios;
    private final UsuarioPapelRepository papeis;
    private final UsuarioService usuarioService;

    public BootstrapAdmin(AppProperties props, UsuarioRepository usuarios, UsuarioPapelRepository papeis, UsuarioService usuarioService) {
        this.props = props;
        this.usuarios = usuarios;
        this.papeis = papeis;
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = props.bootstrap().adminEmail();
        String senha = props.bootstrap().adminSenha();
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) return;
        if (papeis.contarVigentes(Papeis.ADMINISTRADOR) > 0) return;
        if (senha.length() < 8 || usuarios.existsByEmailIgnoreCase(UsuarioService.normalizarEmail(email))) {
            log.warn("ADMIN_EMAIL/ADMIN_SENHA ignorados: senha curta (mín. 8) ou e-mail já cadastrado.");
            return;
        }
        usuarioService.criar("Administrador", email, null, senha, "ATIVO", List.of(Papeis.ADMINISTRADOR, Papeis.CIDADAO), null);
        log.info("Administrador inicial criado: {}", email);
    }
}
