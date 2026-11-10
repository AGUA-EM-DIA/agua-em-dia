package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.ValidacaoReparo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValidacaoReparoRepository extends JpaRepository<ValidacaoReparo, Long> {
    boolean existsByConclusaoId(Long conclusaoId);
}
