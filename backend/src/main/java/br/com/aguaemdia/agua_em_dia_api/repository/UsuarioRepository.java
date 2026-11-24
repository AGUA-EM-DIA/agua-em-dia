package br.com.aguaemdia.agua_em_dia_api.repository;

import br.com.aguaemdia.agua_em_dia_api.entity.Usuario; //importa a entidade usuario para utilizar na interface
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    //VERIFICA SE JÁ EXISTE EMAIL, NOME DE USUARIO E CPF CADASTRADOS

    boolean existsByEmail(String email);
    boolean existsByNomeUsuario(String nomeUsuario);
    boolean existsByCpf(String cpf);

    //BUSCA O USUÁRIO PELO E-MAIL (usado no login)
    Optional<Usuario> findByEmail(String email);

}
