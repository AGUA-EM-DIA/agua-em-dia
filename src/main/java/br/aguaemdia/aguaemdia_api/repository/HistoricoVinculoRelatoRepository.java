package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.HistoricoVinculoRelato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoVinculoRelatoRepository extends JpaRepository<HistoricoVinculoRelato, Long> {
    List<HistoricoVinculoRelato> findByVinculoId(Long vinculoId);
}
