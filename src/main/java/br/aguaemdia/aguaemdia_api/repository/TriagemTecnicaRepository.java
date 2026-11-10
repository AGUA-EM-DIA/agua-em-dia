package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.TriagemTecnica;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriagemTecnicaRepository extends JpaRepository<TriagemTecnica, Long> {
    Optional<TriagemTecnica> findByVazamentoIdAndSituacao(Long vazamentoId, String situacao);
    List<TriagemTecnica> findBySituacaoOrderByCriadoEmAsc(String situacao);
}
