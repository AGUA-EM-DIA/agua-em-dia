package br.com.aguaemdia.agua_em_dia_api.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/** Cria o "gerador" e o "conferidor" de tokens JWT, assinados com um segredo (HS256) que vem de fora do código. */
@Configuration
public class JwtConfig {

    @Bean
    public SecretKey chaveAssinaturaJwt(@Value("${app.jwt.segredo:}") String segredo) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "Defina o segredo do JWT com pelo menos 32 caracteres: variável de ambiente JWT_SECRET "
                            + "(propriedade app.jwt.segredo). Veja o README.");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey chaveAssinaturaJwt) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chaveAssinaturaJwt));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey chaveAssinaturaJwt) {
        return NimbusJwtDecoder.withSecretKey(chaveAssinaturaJwt).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
