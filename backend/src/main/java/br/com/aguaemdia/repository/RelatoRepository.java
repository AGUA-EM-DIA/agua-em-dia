package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.PorteVazamento;
import br.com.aguaemdia.domain.Relato;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelatoRepository extends JpaRepository<Relato, Long> {

    Page<Relato> findByUsuarioIdOrderByEnviadoEmDesc(Long usuarioId, Pageable pageable);

    Optional<Relato> findByIdAndUsuarioId(Long id, Long usuarioId);

    Optional<Relato> findByProtocolo(String protocolo);

    boolean existsByProtocolo(String protocolo);

    List<Relato> findByVazamentoId(Long vazamentoId);

    boolean existsByVazamentoIdAndUsuarioId(Long vazamentoId, Long usuarioId);

    long countByUsuarioId(Long usuarioId);

    long countByVazamentoId(Long vazamentoId);

    @Query("select count(distinct r.usuarioId) from Relato r where r.vazamentoId = :vid")
    long contarCidadaosDistintos(@Param("vid") Long vazamentoId);

    @Query("select distinct r.usuarioId from Relato r where r.vazamentoId = :vid")
    List<Long> autoresDistintos(@Param("vid") Long vazamentoId);

    @Query("select min(r.enviadoEm) from Relato r where r.vazamentoId = :vid")
    OffsetDateTime primeiroEnvio(@Param("vid") Long vazamentoId);

    /** Portes informados no vazamento, do maior para o menor (usar PageRequest.of(0, 1) para o maior). */
    @Query("""
           select p from Relato r join PorteVazamento p on p.id = r.porteInformadoId
           where r.vazamentoId = :vid order by p.ordem desc
           """)
    List<PorteVazamento> portesInformados(@Param("vid") Long vazamentoId, Pageable pageable);

    @Query("""
           select count(distinct r.vazamentoId) from Relato r join Vazamento v on v.id = r.vazamentoId
           where r.usuarioId = :uid and v.status in :status
           """)
    long contarVazamentosDoUsuario(@Param("uid") Long usuarioId, @Param("status") Collection<String> status);

    @Query("""
           select count(distinct a.id) from Relato r join Atendimento a on a.vazamentoId = r.vazamentoId
           where r.usuarioId = :uid and a.status = 'CONCLUIDO'
           """)
    long contarAtendimentosConcluidos(@Param("uid") Long usuarioId);
}
