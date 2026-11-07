package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.HistoricoAlteracaoRelato;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoAlteracaoRelatoRepository extends JpaRepository<HistoricoAlteracaoRelato, Long> {
    List<HistoricoAlteracaoRelato> findByRelatoIdOrderByAlteradoEmDesc(Long relatoId);
}
