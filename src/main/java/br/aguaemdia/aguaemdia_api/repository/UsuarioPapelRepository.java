package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.UsuarioPapel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioPapelRepository extends JpaRepository<UsuarioPapel, Long> {

    @Query("""
           select p.nome from UsuarioPapel up join Papel p on p.id = up.papelId
           where up.usuarioId = :usuarioId and up.revogadoEm is null and p.ativo = true
           """)
    List<String> nomesVigentes(@Param("usuarioId") Long usuarioId);

    Optional<UsuarioPapel> findByUsuarioIdAndPapelIdAndRevogadoEmIsNull(Long usuarioId, Long papelId);

    List<UsuarioPapel> findByUsuarioIdAndRevogadoEmIsNull(Long usuarioId);

    /** Quantos usuários ATIVOS possuem o papel (evita remover o último administrador). */
    @Query("""
           select count(up) from UsuarioPapel up join Papel p on p.id = up.papelId
           join Usuario u on u.id = up.usuarioId
           where p.nome = :papel and up.revogadoEm is null and u.situacao = 'ATIVO'
           """)
    long contarVigentes(@Param("papel") String papel);
}
