package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.VinculoDuplicidade;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VinculoDuplicidadeRepository extends JpaRepository<VinculoDuplicidade, Long> {
    Optional<VinculoDuplicidade> findByOrigemIdAndRevertidoEmIsNull(Long origemId);
    List<VinculoDuplicidade> findByCanonicoIdAndRevertidoEmIsNull(Long canonicoId);
    List<VinculoDuplicidade> findByOrigemIdOrCanonicoIdOrderByAssociadoEmDesc(Long origemId, Long canonicoId);
}
