package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.AtribuicaoEquipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtribuicaoEquipeRepository extends JpaRepository<AtribuicaoEquipe, Long> {
    Optional<AtribuicaoEquipe> findByAtendimentoIdAndEncerradoEmIsNull(Long atendimentoId);
    List<AtribuicaoEquipe> findByAtendimentoIdOrderByAtribuidoEmAsc(Long atendimentoId);
    long countByEquipeIdAndEncerradoEmIsNull(Long equipeId);
}
