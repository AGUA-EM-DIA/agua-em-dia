package br.com.aguaemdia.agua_em_dia_api.controller;

import br.com.aguaemdia.agua_em_dia_api.dto.IdentificacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.service.ArmazenamentoImagemService;
import br.com.aguaemdia.agua_em_dia_api.service.GeminiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** Identificação do vazamento pela foto (Gemini). A foto enviada aqui não é guardada. */
@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final GeminiService gemini;
    private final ArmazenamentoImagemService imagens;

    public IaController(GeminiService gemini, ArmazenamentoImagemService imagens) {
        this.gemini = gemini;
        this.imagens = imagens;
    }

    @PostMapping(value = "/identificar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public IdentificacaoDTO identificar(@RequestPart("foto") MultipartFile foto) {
        ArmazenamentoImagemService.Imagem imagem = imagens.validar(foto);
        return gemini.identificar(imagem.bytes(), imagem.tipo().getMime());
    }
}
