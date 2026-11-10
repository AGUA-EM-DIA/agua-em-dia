package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.MembroEquipe;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembroEquipeRepository extends JpaRepository<MembroEquipe, Long> {
    Optional<MembroEquipe> findByUsuarioIdAndFimVinculoIsNull(Long usuarioId);
    List<MembroEquipe> findByEquipeIdAndFimVinculoIsNull(Long equipeId);
}
