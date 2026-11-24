
package br.com.aguaemdia.agua_em_dia_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                // A API usa token JWT no cabeçalho (não usa cookie de sessão), então não há CSRF a proteger.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Arquivos do app (HTML, CSS, JS) e fotos enviadas pelos cidadãos
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/img/**", "/favicon.ico", "/uploads/**").permitAll()
                        // Rotas públicas: cadastro, login, recuperação de senha, login com Google e configuração do app
                        .requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/config").permitAll()
                        // A permissão desta rota (chave de serviço ou modo demonstração) é conferida no controller
                        .requestMatchers(HttpMethod.PATCH, "/api/vazamentos/*/situacao").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(respostaNaoAutenticado()));

        return http.build();
    }

    /** Token ausente, inválido ou expirado: responde 401 em JSON, no mesmo formato dos outros erros. */
    private AuthenticationEntryPoint respostaNaoAutenticado() {
        return (request, response, excecao) -> {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getOutputStream().write(
                    "{\"mensagem\":\"Sessão inválida ou expirada. Entre novamente.\"}".getBytes(StandardCharsets.UTF_8));
        };
    }
}
