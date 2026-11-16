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
    
    /** Valida os 11 dígitos do CPF, incluindo os dígitos verificadores. */
    public static boolean cpfValido(String cpf) {
        if (cpf == null) return false;

        String numero = cpf.replaceAll("\\D", "");

        if (!numero.matches("\\d{11}")) return false;

        if (numero.chars().distinct().count() == 1) return false;

        int soma = 0;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(numero.charAt(i)) * (10 - i);
        }

        int primeiro = (soma * 10) % 11;
        if (primeiro == 10) primeiro = 0;

        if (primeiro != Character.getNumericValue(numero.charAt(9))) {
            return false;
        }

        soma = 0;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(numero.charAt(i)) * (11 - i);
        }

        int segundo = (soma * 10) % 11;
        if (segundo == 10) segundo = 0;

        return segundo == Character.getNumericValue(numero.charAt(10));
    }

}
