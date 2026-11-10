package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.ConclusaoServico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConclusaoServicoRepository extends JpaRepository<ConclusaoServico, Long> {
    Optional<ConclusaoServico> findFirstByAtendimentoIdOrderByInformadoEmDesc(Long atendimentoId);
}
