package br.com.aguaemdia.agua_em_dia_api.security;

import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Gera o token de acesso do cidadão. O id do usuário vai no campo "sub" do token. */
@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long horasDeValidade;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.horas:12}") long horasDeValidade) {
        this.encoder = encoder;
        this.horasDeValidade = horasDeValidade;
    }

    public Jwt gerar(Usuario usuario) {
        Instant agora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("agua-em-dia")
                .issuedAt(agora)
                .expiresAt(agora.plus(horasDeValidade, ChronoUnit.HOURS))
                .subject(String.valueOf(usuario.getId()))
                .build();
        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(cabecalho, claims));
    }
}
