package br.com.aguaemdia.agua_em_dia_api.repository;

import br.com.aguaemdia.agua_em_dia_api.entity.Apoio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ApoioRepository extends JpaRepository<Apoio, Long> {

    /** Total de reforços de um vazamento. */
    interface TotalPorVazamento {
        Long getVazamentoId();

        Long getTotal();
    }

    @Query("select a.vazamento.id as vazamentoId, count(a) as total from Apoio a group by a.vazamento.id")
    List<TotalPorVazamento> totaisPorVazamento();

    @Query("select a.vazamento.id from Apoio a where a.usuario.id = :usuarioId")
    Set<Long> idsDosVazamentosApoiadosPor(@Param("usuarioId") Long usuarioId);

    boolean existsByUsuarioIdAndVazamentoId(Long usuarioId, Long vazamentoId);

    long countByVazamentoId(Long vazamentoId);

    long countByUsuarioId(Long usuarioId);
}
