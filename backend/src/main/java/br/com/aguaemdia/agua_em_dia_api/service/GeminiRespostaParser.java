package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.IdentificacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.PorteVazamento;
import br.com.aguaemdia.agua_em_dia_api.entity.TipoVazamento;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lê o texto JSON que o Gemini devolve com a classificação da foto.
 * O formato é fixo e pequeno (4 campos), então basta ler com expressões regulares.
 * Qualquer valor fora do esperado faz o resultado ser descartado.
 */
public final class GeminiRespostaParser {

    private static final Pattern TIPO = Pattern.compile("\"tipo\"\\s*:\\s*\"([A-Z_]+)\"");
    private static final Pattern PORTE = Pattern.compile("\"porte\"\\s*:\\s*\"([A-Z_]+)\"");
    private static final Pattern CONFIANCA = Pattern.compile("\"confianca\"\\s*:\\s*(-?[0-9]+(?:\\.[0-9]+)?)");
    private static final Pattern MOTIVO = Pattern.compile("\"motivo\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");

    private GeminiRespostaParser() {
    }

    public static Optional<IdentificacaoDTO> interpretar(String texto) {
        if (texto == null) {
            return Optional.empty();
        }
        String json = texto.trim().replaceFirst("(?i)^```(?:json)?", "").replaceFirst("```$", "").trim();

        String tipo = primeiro(TIPO, json);
        String porte = primeiro(PORTE, json);
        String confianca = primeiro(CONFIANCA, json);
        if (tipo == null || porte == null || confianca == null || !tipoValido(tipo) || !porteValido(porte)) {
            return Optional.empty();
        }

        double c = Math.max(0, Math.min(1, Double.parseDouble(confianca)));
        String motivo = primeiro(MOTIVO, json);
        motivo = motivo == null ? "" : descapar(motivo);
        if (motivo.length() > 140) {
            motivo = motivo.substring(0, 140);
        }
        return Optional.of(new IdentificacaoDTO(tipo, porte, c, motivo));
    }

    private static boolean tipoValido(String tipo) {
        if (tipo.equals("NENHUM")) {
            return true;
        }
        for (TipoVazamento t : TipoVazamento.values()) {
            if (t.name().equals(tipo)) {
                return true;
            }
        }
        return false;
    }

    private static boolean porteValido(String porte) {
        for (PorteVazamento p : PorteVazamento.values()) {
            if (p.name().equals(porte)) {
                return true;
            }
        }
        return false;
    }

    private static String primeiro(Pattern padrao, String texto) {
        Matcher m = padrao.matcher(texto);
        return m.find() ? m.group(1) : null;
    }

    /** Desfaz as sequências de escape de uma string JSON (\", \\, \n, \t, \/ e \\uXXXX). */
    private static String descapar(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\\' || i + 1 >= s.length()) {
                sb.append(c);
                continue;
            }
            char n = s.charAt(++i);
            switch (n) {
                case 'n' -> sb.append(' ');
                case 't' -> sb.append(' ');
                case 'u' -> {
                    if (i + 4 < s.length() && s.substring(i + 1, i + 5).matches("[0-9a-fA-F]{4}")) {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                        i += 4;
                    }
                }
                default -> sb.append(n);
            }
        }
        return sb.toString().trim();
    }
}
