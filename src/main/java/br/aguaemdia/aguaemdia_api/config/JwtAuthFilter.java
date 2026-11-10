package br.com.aguaemdia.config;

import br.com.aguaemdia.common.UsuarioLogado;
import br.com.aguaemdia.repository.UsuarioPapelRepository;
import br.com.aguaemdia.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Lê "Authorization: Bearer ...", valida o token e recarrega usuário e papéis do banco:
 * desativação de conta e revogação de papel passam a valer imediatamente (RNF003).
 * Não é um @Component de propósito: é registrado só dentro da cadeia do Spring Security.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarios;
    private final UsuarioPapelRepository papeis;

    public JwtAuthFilter(JwtService jwtService, UsuarioRepository usuarios, UsuarioPapelRepository papeis) {
        this.jwtService = jwtService;
        this.usuarios = usuarios;
        this.papeis = papeis;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            jwtService.validar(header.substring(7).trim()).flatMap(usuarios::findById)
                    .filter(u -> "ATIVO".equals(u.getSituacao()))
                    .ifPresent(u -> {
                        List<String> nomes = papeis.nomesVigentes(u.getId());
                        Set<String> conjunto = new HashSet<>(nomes);
                        UsuarioLogado principal = new UsuarioLogado(u.getId(), u.getEmail(), conjunto);
                        List<SimpleGrantedAuthority> autoridades = nomes.stream()
                                .map(n -> new SimpleGrantedAuthority("ROLE_" + n)).toList();
                        SecurityContextHolder.getContext()
                                .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, autoridades));
                    });
        }
        chain.doFilter(request, response);
    }
}
