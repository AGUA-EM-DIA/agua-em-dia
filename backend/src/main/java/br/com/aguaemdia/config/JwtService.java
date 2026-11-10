package br.com.aguaemdia.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

/** Emite e valida o JWT. O token só carrega o id do usuário; papéis são relidos do banco a cada requisição. */
@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracaoMinutos;
    private final Clock clock;

    public JwtService(AppProperties props, Clock clock) {
        byte[] bytes = props.jwt().segredo().getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.segredo deve ter pelo menos 32 bytes em UTF-8.");
        }

        if(props.jwt().expiracaoMinutos() <= 0){
            throw new IllegalStateException("app.jwt.expiracao-minutos deve ser maior que zero.");
        }
        this.chave = Keys.hmacShaKeyFor(bytes);
        this.expiracaoMinutos = props.jwt().expiracaoMinutos();
        this.clock = clock;
    }

    public Instant expiraEm(Instant emitidoEm) {
        return emitidoEm.plus(expiracaoMinutos, ChronoUnit.MINUTES);
    }

    public String gerar(Long usuarioId, Instant emitidoEm) {
        return Jwts.builder()
                .subject(String.valueOf(usuarioId))
                .issuedAt(Date.from(emitidoEm))
                .expiration(Date.from(expiraEm(emitidoEm)))
                .signWith(chave)
                .compact();
    }

    public String gerar(Long usuarioId) {
        return gerar(usuarioId, clock.instant());
    }

    /** Devolve o id do usuário se a assinatura e a validade estiverem corretas. */
    public Optional<Long> validar(String token) {
        try {
            String sub = Jwts.parser().verifyWith(chave).build().parseSignedClaims(token).getPayload().getSubject();
            return Optional.of(Long.valueOf(sub));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
