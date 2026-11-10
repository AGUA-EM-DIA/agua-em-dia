package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.SolicitacaoReabertura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoReaberturaRepository extends JpaRepository<SolicitacaoReabertura, Long> {
    boolean existsByVazamentoIdAndSituacao(Long vazamentoId, String situacao);
    List<SolicitacaoReabertura> findBySituacaoOrderByCriadoEmAsc(String situacao);
}
