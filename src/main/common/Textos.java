package br.com.aguaemdia.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

public final class Textos {
    private Textos() {}

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALFABETO_PROTOCOLO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    /** Remove espaços nas pontas; devolve null se ficar vazio. */
    public static String limpar(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    /** Minúsculas, sem acentos e sem espaços repetidos (para comparar nomes de bairro). */
    public static String normalizar(String s) {
        if (s == null) return null;
        String semAcento = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
    }

    public static String truncar(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    public static String sha256Hex(String valor) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(valor.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Token aleatório de 256 bits, seguro para URL. Só o hash vai para o banco. */
    public static String tokenAleatorio() {
        byte[] b = new byte[32];
        RANDOM.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }

    /** Parte aleatória do protocolo (sem caracteres ambíguos como 0/O e 1/I). */
    public static String sufixoProtocolo() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) sb.append(ALFABETO_PROTOCOLO.charAt(RANDOM.nextInt(ALFABETO_PROTOCOLO.length())));
        return sb.toString();
    }
}
