package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Vazamento;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VazamentoRepository extends JpaRepository<Vazamento, Long> {

    /** Trava a linha (UPDLOCK) para serializar mudanças de estado e de intervalos da mesma ocorrência. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from Vazamento v where v.id = :id")
    Optional<Vazamento> findByIdParaAtualizar(@Param("id") Long id);

    List<Vazamento> findByStatusIn(Collection<String> status);

    long countByStatusIn(Collection<String> status);

    List<Vazamento> findByStatusAndResolvidoEmBetween(String status, OffsetDateTime de, OffsetDateTime ate);

    interface Candidato {
        Long getId();
        Double getDistancia();
    }

    /** Candidatos a duplicidade: raio (m) + janela entre relatos (min), mesma categoria, ainda em aberto. */
    @Query(value = """
            SELECT c.id AS id, CAST(lc.geo.STDistance(lo.geo) AS FLOAT) AS distancia
            FROM Vazamento c
            JOIN Localizacao lc ON lc.id = c.localizacao_atual_id
            JOIN Vazamento o ON o.id = :origemId
            JOIN Localizacao lo ON lo.id = o.localizacao_atual_id
            WHERE c.id <> o.id
              AND c.categoria_id = o.categoria_id
              AND c.status NOT IN ('DUPLICADA','CANCELADA','RESOLVIDA')
              AND lc.geo.STDistance(lo.geo) <= :raio
              AND EXISTS (SELECT 1 FROM Relato rc
                          WHERE rc.vazamento_id = c.id
                            AND EXISTS (SELECT 1 FROM Relato ro
                                        WHERE ro.vazamento_id = o.id
                                          AND ABS(DATEDIFF(MINUTE, rc.enviado_em, ro.enviado_em)) <= :janelaMinutos))
            ORDER BY distancia
            """, nativeQuery = true)
    List<Candidato> buscarCandidatosDuplicidade(@Param("origemId") Long origemId, @Param("raio") double raioMetros,
            @Param("janelaMinutos") long janelaMinutos);

    /** Vizinhos ativos e independentes dentro do raio de concentração (fator G). */
    @Query(value = """
            SELECT COUNT(*)
            FROM Vazamento c
            JOIN Localizacao lc ON lc.id = c.localizacao_atual_id
            JOIN Vazamento o ON o.id = :id
            JOIN Localizacao lo ON lo.id = o.localizacao_atual_id
            WHERE c.id <> o.id
              AND c.categoria_id = o.categoria_id
              AND c.status IN (:statuses)
              AND lc.geo.STDistance(lo.geo) <= :raio
            """, nativeQuery = true)
    int contarVizinhosAtivos(@Param("id") Long id, @Param("raio") double raioMetros,
            @Param("statuses") Collection<String> statuses);

    /** Atualiza só os campos derivados, sem mexer na versão (não conflita com mudanças de estado). */
    @Modifying
    @Query("update Vazamento v set v.prioridadeAtual = :p, v.prioridadeCalculadaEm = :t where v.id = :id")
    int atualizarPrioridade(@Param("id") Long id, @Param("p") BigDecimal pontuacao, @Param("t") OffsetDateTime quando);
}
