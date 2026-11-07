package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.ContatoRetorno;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContatoRetornoRepository extends JpaRepository<ContatoRetorno, Long> {
    Optional<ContatoRetorno> findByAvaliacaoId(Long avaliacaoId);
    List<ContatoRetorno> findBySituacaoNotOrderByCriadoEmAsc(String situacao);
}
