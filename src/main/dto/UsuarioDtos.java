package br.com.aguaemdia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public final class UsuarioDtos {
    private UsuarioDtos() {}

    public record CriarUsuarioRequest(
            @NotBlank @Size(max = 150) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @Size(max = 30) String telefone,
            @NotEmpty Set<String> papeis) {}

    public record PapelRequest(@NotBlank String papel) {}

    public record AtualizarPerfilRequest(@NotBlank @Size(max = 150) String nome, @Size(max = 30) String telefone) {}

    public record ConfirmarSenhaRequest(@NotBlank String senha) {}

    public record EstatisticasCidadao(long totalRelatos, long ocorrenciasEmAndamento, long atendimentosConcluidos) {}
}
