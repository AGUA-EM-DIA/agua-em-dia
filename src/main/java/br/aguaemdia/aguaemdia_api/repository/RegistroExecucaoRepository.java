package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.RegistroExecucao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroExecucaoRepository extends JpaRepository<RegistroExecucao, Long> {
    List<RegistroExecucao> findByAtendimentoIdOrderByRegistradoEmAsc(Long atendimentoId);
}
