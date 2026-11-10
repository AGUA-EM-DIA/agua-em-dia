package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Usuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    @Query("""
           select u from Usuario u
           where (:q is null or lower(u.nome) like :q or lower(u.email) like :q)
             and (:situacao is null or u.situacao = :situacao)
           order by u.nome
           """)
    Page<Usuario> buscar(@Param("q") String q, @Param("situacao") String situacao, Pageable pageable);
}
