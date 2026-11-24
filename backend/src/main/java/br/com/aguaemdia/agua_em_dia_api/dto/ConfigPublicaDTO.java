package br.com.aguaemdia.agua_em_dia_api.dto;

/** Configuração pública que o frontend precisa conhecer. Nunca inclui segredos (só chaves feitas para ficar no navegador). */
public record ConfigPublicaDTO(boolean demo, String mapsKey, boolean iaDisponivel, String googleClientId) {
}
