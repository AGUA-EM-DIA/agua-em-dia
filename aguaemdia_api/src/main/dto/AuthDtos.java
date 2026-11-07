package br.com.aguaemdia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.Set;

public final class AuthDtos {
    private AuthDtos() {}

    public static final String REGEX_SENHA = "^(?=.*[A-Za-z])(?=.*\\d).+$";
    public static final String MSG_SENHA = "A senha deve ter de 8 a 72 caracteres, com letras e números.";

    public record RegistroRequest(
            @NotBlank @Size(max = 150) String nome,
            @NotBlank @Email @Size(max = 254) String email,
            @Size(max = 30) String telefone,
            @NotBlank @Size(min = 8, max = 72, message = MSG_SENHA) @Pattern(regexp = REGEX_SENHA, message = MSG_SENHA) String senha) {}

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String senha) {}

    public record TokenRequest(@NotBlank String token) {}

    public record EmailRequest(@NotBlank @Email String email) {}

    public record RedefinirSenhaRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 8, max = 72, message = MSG_SENHA) @Pattern(regexp = REGEX_SENHA, message = MSG_SENHA) String novaSenha) {}

    public record MensagemResponse(String mensagem) {}

    public record UsuarioResumo(Long id, String nome, String email, String telefone, String situacao, Set<String> papeis) {}

    public record LoginResponse(String token, String tipo, OffsetDateTime expiraEm, UsuarioResumo usuario) {}
}
