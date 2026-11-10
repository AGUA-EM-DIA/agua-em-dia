package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Atendimento;
import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Atendimento a where a.id = :id")
    Optional<Atendimento> findByIdParaAtualizar(@Param("id") Long id);

    List<Atendimento> findByVazamentoIdOrderByCriadoEmDesc(Long vazamentoId);

    Optional<Atendimento> findFirstByVazamentoIdAndStatusIn(Long vazamentoId, Collection<String> status);

    @Query("""
           select a from Atendimento a
           where (:status is null or a.status = :status)
           order by a.criadoEm desc
           """)
    Page<Atendimento> filtrar(@Param("status") String status, Pageable pageable);

    @Query("""
           select a from Atendimento a join AtribuicaoEquipe t on t.atendimentoId = a.id
           where t.equipeId = :equipeId and t.encerradoEm is null and a.status in :status
           order by a.criadoEm
           """)
    List<Atendimento> daEquipe(@Param("equipeId") Long equipeId, @Param("status") Collection<String> status);
}
