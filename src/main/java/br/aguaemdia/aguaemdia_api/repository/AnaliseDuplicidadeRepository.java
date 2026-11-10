package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.AnaliseDuplicidade;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnaliseDuplicidadeRepository extends JpaRepository<AnaliseDuplicidade, Long> {
    Optional<AnaliseDuplicidade> findByOrigemIdAndCandidatoId(Long origemId, Long candidatoId);
    List<AnaliseDuplicidade> findByOrigemId(Long origemId);
}
