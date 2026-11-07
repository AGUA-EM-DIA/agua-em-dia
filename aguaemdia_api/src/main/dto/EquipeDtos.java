package br.com.aguaemdia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public final class EquipeDtos {
    private EquipeDtos() {}

    public record EquipeRequest(@NotBlank @Size(max = 100) String nome) {}

    public record MembroRequest(@NotNull Long usuarioId) {}

    public record MembroResponse(Long usuarioId, String nome) {}

    public record EquipeResponse(Long id, String nome, String situacao, List<MembroResponse> membros) {}
}
