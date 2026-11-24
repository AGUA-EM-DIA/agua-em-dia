package br.com.aguaemdia.agua_em_dia_api.controller;

import br.com.aguaemdia.agua_em_dia_api.dto.AquariMensagemDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.AquariRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.service.AquariService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Assistente Aquari: recebe uma pergunta de texto e responde. */
@RestController
@RequestMapping("/api/aquari")
public class AquariController {

    private final AquariService aquari;

    public AquariController(AquariService aquari) {
        this.aquari = aquari;
    }

    @PostMapping("/mensagens")
    public AquariRespostaDTO conversar(@Valid @RequestBody AquariMensagemDTO mensagem) {
        return new AquariRespostaDTO(aquari.responder(mensagem.texto()));
    }
}
