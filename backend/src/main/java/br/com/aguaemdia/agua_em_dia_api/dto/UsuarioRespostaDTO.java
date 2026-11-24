package br.com.aguaemdia.agua_em_dia_api.dto;

import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;

/** Dados do cidadão devolvidos ao frontend. Nunca inclui a senha; o CPF sai mascarado. */
public record UsuarioRespostaDTO(Long id, String nome, String nomeUsuario, String email, String telefone,
                                 String cpfMascarado) {

    public static UsuarioRespostaDTO de(Usuario u) {
        String cpf = u.getCpf() == null ? "" : u.getCpf().replaceAll("\\D", "");
        String mascarado = cpf.length() == 11 ? "***.***.***-" + cpf.substring(9) : "";
        return new UsuarioRespostaDTO(u.getId(), u.getNome(), u.getNomeUsuario(), u.getEmail(), u.getTelefone(), mascarado);
    }
}
