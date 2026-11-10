package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.IntervaloEstimativa;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IntervaloEstimativaRepository extends JpaRepository<IntervaloEstimativa, Long> {

    List<IntervaloEstimativa> findByVazamentoIdAndSituacaoOrderByInicioAsc(Long vazamentoId, String situacao);

    List<IntervaloEstimativa> findByVazamentoIdInAndSituacaoOrderByInicioAsc(Collection<Long> ids, String situacao);

    Optional<IntervaloEstimativa> findByVazamentoIdAndFimIsNullAndSituacao(Long vazamentoId, String situacao);

    /** Pares [intervalo, vazamento] que tocam o período, ignorando duplicadas e canceladas (sem dupla contagem). */
    @Query("""
           select i, v from IntervaloEstimativa i join Vazamento v on v.id = i.vazamentoId
           where i.situacao = 'ATIVO' and v.status not in ('DUPLICADA','CANCELADA')
             and i.inicio < :ate and (i.fim is null or i.fim > :de)
           """)
    List<Object[]> tocandoPeriodo(@Param("de") OffsetDateTime de, @Param("ate") OffsetDateTime ate);

    @Query("""
           select coalesce(sum(i.vazaoLH), 0) from IntervaloEstimativa i join Vazamento v on v.id = i.vazamentoId
           where i.fim is null and i.situacao = 'ATIVO' and v.status in :status
           """)
    BigDecimal somaVazaoEmCurso(@Param("status") Collection<String> status);

    @Query("select i from IntervaloEstimativa i where i.vazamentoId in :ids and i.fim is null and i.situacao = 'ATIVO'")
    List<IntervaloEstimativa> emCursoDe(@Param("ids") Collection<Long> ids);
}
