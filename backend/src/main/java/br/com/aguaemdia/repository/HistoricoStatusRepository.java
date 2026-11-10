package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.HistoricoStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoStatusRepository extends JpaRepository<HistoricoStatus, Long> {
    List<HistoricoStatus> findByEntidadeTipoAndEntidadeIdOrderByAlteradoEmAsc(String entidadeTipo, Long entidadeId);
}
