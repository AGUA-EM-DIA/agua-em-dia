package br.com.aguaemdia.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String timezone,
        String urlBaseFront,
        List<String> corsOrigens,
        Jwt jwt,
        Armazenamento armazenamento,
        Mail mail,
        Bootstrap bootstrap,
        Notificacoes notificacoes) {

    public record Jwt(String segredo, long expiracaoMinutos) {}
    public record Armazenamento(String diretorio) {}
    public record Mail(boolean enabled, String remetente) {}
    public record Bootstrap(String adminEmail, String adminSenha) {}
    public record Notificacoes(long intervaloMs) {}
}
