package br.com.aguaemdia.config;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    /** Relógio em UTC com precisão de milissegundos (compatível com DATETIMEOFFSET(3)). */
    @Bean
    public Clock clock() {
        return Clock.tick(Clock.systemUTC(), Duration.ofMillis(1));
    }

    /** Fuso usado para "dia civil" nos indicadores (documentação 5.3: America/Recife). */
    @Bean
    public ZoneId zonaDeNegocio(AppProperties props) {
        return ZoneId.of(props.timezone());
    }
}
