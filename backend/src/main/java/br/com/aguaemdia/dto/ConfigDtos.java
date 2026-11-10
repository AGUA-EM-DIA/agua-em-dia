package br.com.aguaemdia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public final class ConfigDtos {
    private ConfigDtos() {}

    public record ParametroRequest(@NotBlank String valor) {}

    public record ParametroResponse(String chave, String valor, String descricao) {}

    public record PorteResponse(Long id, String codigo, String nome, Integer ordem, BigDecimal vazaoBaseLitrosPorHora, boolean ativo) {}

    public record AtualizarPorteRequest(@DecimalMin("0.0") BigDecimal vazaoBaseLitrosPorHora, Boolean ativo) {}

    public record PrazoRequest(@NotNull Long porteId, @NotNull Long infraestruturaId, @NotNull @Min(1) Integer horas) {}

    public record PrazoResponse(Long porteId, String porte, Long infraestruturaId, String infraestrutura, Integer horas, OffsetDateTime vigenteDesde) {}

    public record Referencia(Long id, String codigo, String nome) {}

    public record Referencias(List<Referencia> categorias, List<Referencia> portes, List<Referencia> infraestruturas) {}
}
