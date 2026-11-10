package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.AvaliacaoEmergencia;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoEmergenciaRepository extends JpaRepository<AvaliacaoEmergencia, Long> {
    List<AvaliacaoEmergencia> findByVazamentoIdInOrderByAvaliadoEmDesc(Collection<Long> vazamentoIds);
    boolean existsBySinalizacaoId(Long sinalizacaoId);
}
