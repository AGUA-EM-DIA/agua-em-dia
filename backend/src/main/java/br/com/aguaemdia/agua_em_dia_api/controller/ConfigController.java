package br.com.aguaemdia.agua_em_dia_api.controller;

import br.com.aguaemdia.agua_em_dia_api.dto.ConfigPublicaDTO;
import br.com.aguaemdia.agua_em_dia_api.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** O que o frontend precisa saber ao abrir: modo demonstração, chave pública do Maps e recursos ativos. */
@RestController
@RequestMapping("/api/config")
public class ConfigController {

    private final boolean demo;
    private final String mapsKey;
    private final String googleClientId;
    private final GeminiService gemini;

    public ConfigController(@Value("${app.demo:false}") boolean demo,
                            @Value("${app.google.maps-key:}") String mapsKey,
                            @Value("${app.google.client-id:}") String googleClientId,
                            GeminiService gemini) {
        this.demo = demo;
        this.mapsKey = mapsKey;
        this.googleClientId = googleClientId;
        this.gemini = gemini;
    }

    @GetMapping
    public ConfigPublicaDTO config() {
        return new ConfigPublicaDTO(demo, mapsKey, gemini.disponivel(), googleClientId);
    }
}
