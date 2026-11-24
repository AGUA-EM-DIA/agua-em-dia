package br.com.aguaemdia.agua_em_dia_api.repository;

import br.com.aguaemdia.agua_em_dia_api.entity.Vazamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VazamentoRepository extends JpaRepository<Vazamento, Long> {

    @Query("select v from Vazamento v join fetch v.usuario order by v.criadoEm desc")
    List<Vazamento> listarTodos();

    @Query("select v from Vazamento v join fetch v.usuario where v.usuario.id = :usuarioId order by v.criadoEm desc")
    List<Vazamento> listarDoUsuario(@Param("usuarioId") Long usuarioId);

    @Query("select v from Vazamento v join fetch v.usuario where v.id = :id")
    Optional<Vazamento> buscarPorId(@Param("id") Long id);

    /** Marca como vistas as novidades de todos os vazamentos do usuário. */
    @Modifying(clearAutomatically = true)
    @Query("update Vazamento v set v.novidadeNaoVista = false where v.usuario.id = :usuarioId")
    int marcarNovidadesComoVistas(@Param("usuarioId") Long usuarioId);
}
