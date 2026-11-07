package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    @Query("""
           select a from Auditoria a
           where (:tipo is null or a.entidadeTipo = :tipo)
             and (:entidadeId is null or a.entidadeId = :entidadeId)
             and (:usuarioId is null or a.usuarioId = :usuarioId)
           order by a.criadoEm desc
           """)
    Page<Auditoria> filtrar(@Param("tipo") String tipo, @Param("entidadeId") Long entidadeId,
            @Param("usuarioId") Long usuarioId, Pageable pageable);
}
