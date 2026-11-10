package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Avaliacao;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    boolean existsByAtendimentoIdAndUsuarioId(Long atendimentoId, Long usuarioId);
    List<Avaliacao> findByAtendimentoIdAndUsuarioId(Long atendimentoId, Long usuarioId);
    Page<Avaliacao> findByNotaLessThanEqualOrderByCriadoEmDesc(Short nota, Pageable pageable);
}
