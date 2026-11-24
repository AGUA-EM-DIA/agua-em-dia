package br.com.aguaemdia.agua_em_dia_api.dto;

import br.com.aguaemdia.agua_em_dia_api.validacao.Cpf;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

//Recebe as informações digitadas oelo usuario no frot
public class UsuarioCadastroDTO {

    @NotBlank(message = "Informe seu nome.")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "Escolha um nome de usuário.")
    @Pattern(regexp = "^[a-z0-9._]{3,20}$", message = "Nome de usuário: 3 a 20 letras minúsculas, números, ponto ou _.")
    private String nomeUsuario;

    @NotBlank(message = "Informe seu CPF.")
    @Cpf(message = "Esse CPF não parece válido.")
    @Size(max = 14)
    private String cpf;

    @NotBlank(message = "Informe seu e-mail.")
    @Email(message = "Esse e-mail não parece válido.")
    @Size(max = 150)
    private String email;

    // Celular com DDD: 10 ou 11 dígitos, com ou sem máscara
    @NotBlank(message = "Informe seu celular com DDD.")
    @Pattern(regexp = "^\\D*(\\d\\D*){10,11}$", message = "Celular inválido. Use DDD e número.")
    @Size(max = 20)
    private String telefone;

    @NotBlank(message = "Crie uma senha.")
    @Size(min = 8 , max = 100, message = "A senha precisa ter de 8 a 100 caracteres.")
    private String senha;

    @AssertTrue(message = "É preciso aceitar os termos de uso e a política de privacidade.")
    private boolean aceitouTermos;

     public String getNome() {
         return nome;
     }

     public void setNome(String nome) {
         this.nome = nome;
     }

     public String getNomeUsuario() {
         return nomeUsuario;
     }

     public void setNomeUsuario(String nomeUsuario) {
         this.nomeUsuario = nomeUsuario;
     }

     public String getCpf() {
         return cpf;
     }

     public void setCpf(String cpf) {
         this.cpf = cpf;
     }

     public String getEmail() {
         return email;
     }

     public void setEmail(String email) {
         this.email = email;
     }

     public String getTelefone() {
         return telefone;
     }

     public void setTelefone(String telefone) {
         this.telefone = telefone;
     }

     public String getSenha() {
         return senha;
     }

     public void setSenha(String senha) {
         this.senha = senha;
     }

     public boolean isAceitouTermos() {
         return aceitouTermos;
     }

     public void setAceitouTermos(boolean aceitouTermos) {
         this.aceitouTermos = aceitouTermos;
     }

 }
