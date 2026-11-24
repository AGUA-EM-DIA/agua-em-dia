package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.IdentificacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Identifica o tipo e o tamanho do vazamento a partir de uma foto, usando a API do Gemini.
 * A chave fica no servidor (variável GEMINI_API_KEY) e nunca vai para o navegador.
 * Sem chave configurada, o recurso fica indisponível e o app pede para escolher o tipo manualmente.
 */
@Service
public class GeminiService {

    private static final String PROMPT = "You are a water-utility field inspector in Recife, Brazil. A resident sent one photo. "
            + "Classify the water problem visible in it.\n"
            + "tipo: ADUTORA = burst large main pipe, usually on a major road, huge volume; "
            + "REDE = leak in the street distribution network (water bubbling through asphalt or ground, sunken pavement); "
            + "RAMAL = leak at a house connection, water meter or sidewalk valve box; "
            + "CLANDESTINA = illegal connection (improvised hoses or pipes tapped into the network, no meter); "
            + "DESPERDICIO = visible waste that is not a pipe failure (open hydrant, overflowing tank, hose washing a sidewalk); "
            + "NENHUM = no water leak or waste visible in the photo.\n"
            + "porte (estimated flow): GOTEJAMENTO = drip (~3 L/h); FIO = thin steady stream (~60 L/h); MEDIO = medium flow (~600 L/h); "
            + "FORTE = strong jet (~2400 L/h); JORRO = geyser-like gush (~9000 L/h).\n"
            + "Be conservative: if the image is dark, blurry or ambiguous, lower confianca (0 to 1). "
            + "Write motivo in Brazilian Portuguese, one short sentence (max 140 characters) saying what you see.";

    private final String chave;
    private final String modelo;
    private final RestClient cliente;

    public GeminiService(@Value("${app.gemini.chave:}") String chave,
                         @Value("${app.gemini.modelo:gemini-3.5-flash}") String modelo) {
        this.chave = chave;
        this.modelo = modelo;
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(5));
        fabrica.setReadTimeout(Duration.ofSeconds(30));
        this.cliente = RestClient.builder()
                .requestFactory(fabrica)
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public boolean disponivel() {
        return !chave.isBlank();
    }

    public IdentificacaoDTO identificar(byte[] imagem, String mime) {
        if (!disponivel()) {
            throw NegocioException.indisponivel("A identificação por foto não está ativada neste servidor.");
        }
        Map<String, Object> corpo = Map.of(
                "contents", List.of(Map.of("parts", List.of(
                        Map.of("text", PROMPT),
                        Map.of("inlineData", Map.of("mimeType", mime, "data", Base64.getEncoder().encodeToString(imagem)))))),
                "generationConfig", Map.of(
                        "temperature", 0.2,
                        "responseMimeType", "application/json",
                        "responseSchema", Map.of(
                                "type", "OBJECT",
                                "properties", Map.of(
                                        "tipo", Map.of("type", "STRING",
                                                "enum", List.of("ADUTORA", "REDE", "RAMAL", "CLANDESTINA", "DESPERDICIO", "NENHUM")),
                                        "porte", Map.of("type", "STRING",
                                                "enum", List.of("GOTEJAMENTO", "FIO", "MEDIO", "FORTE", "JORRO")),
                                        "confianca", Map.of("type", "NUMBER"),
                                        "motivo", Map.of("type", "STRING")),
                                "required", List.of("tipo", "porte", "confianca", "motivo"))));
        try {
            Map<?, ?> resposta = cliente.post()
                    .uri("/models/{modelo}:generateContent", modelo)
                    .header("x-goog-api-key", chave)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(corpo)
                    .retrieve()
                    .body(Map.class);
            return GeminiRespostaParser.interpretar(extrairTexto(resposta))
                    .orElseThrow(() -> new NegocioException(HttpStatus.BAD_GATEWAY,
                            "Não consegui entender a análise da foto. Escolha o tipo manualmente."));
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 400 || status == 401 || status == 403) {
                throw NegocioException.indisponivel("O serviço de identificação recusou a chave configurada no servidor.");
            }
            if (status == 429) {
                throw new NegocioException(HttpStatus.TOO_MANY_REQUESTS,
                        "Muitas análises seguidas. Espere um instante e tente de novo.");
            }
            throw new NegocioException(HttpStatus.BAD_GATEWAY, "O serviço de identificação não respondeu (erro " + status + ").");
        } catch (ResourceAccessException e) {
            throw new NegocioException(HttpStatus.BAD_GATEWAY, "O serviço de identificação não respondeu a tempo.");
        }
    }

    /** Junta os textos de candidates[0].content.parts[]. */
    private static String extrairTexto(Map<?, ?> resposta) {
        if (resposta == null || !(resposta.get("candidates") instanceof List<?> candidatos) || candidatos.isEmpty()
                || !(candidatos.get(0) instanceof Map<?, ?> candidato)
                || !(candidato.get("content") instanceof Map<?, ?> conteudo)
                || !(conteudo.get("parts") instanceof List<?> partes)) {
            return null;
        }
        StringBuilder texto = new StringBuilder();
        for (Object parte : partes) {
            if (parte instanceof Map<?, ?> p && p.get("text") instanceof String t) {
                texto.append(t);
            }
        }
        return texto.toString();
    }
}
