package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.MatrizPrazoAtendimento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatrizPrazoAtendimentoRepository extends JpaRepository<MatrizPrazoAtendimento, Long> {
    Optional<MatrizPrazoAtendimento> findByPorteIdAndInfraestruturaIdAndVigenteAteIsNull(Long porteId, Long infraId);
    List<MatrizPrazoAtendimento> findByPorteIdAndVigenteAteIsNull(Long porteId);
    List<MatrizPrazoAtendimento> findByVigenteAteIsNullOrderByPorteIdAscInfraestruturaIdAsc();
}
