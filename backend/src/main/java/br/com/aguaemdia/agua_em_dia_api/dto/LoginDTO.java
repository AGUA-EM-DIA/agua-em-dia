
package br.com.aguaemdia.agua_em_dia_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    //O login é feito somente com e-mail e senha. O CPF não é usado para entrar.
    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Esse e-mail não parece válido.")
    private String email;

    @NotBlank(message = "Informe sua senha cadastrada.")
    private String senha;


    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
