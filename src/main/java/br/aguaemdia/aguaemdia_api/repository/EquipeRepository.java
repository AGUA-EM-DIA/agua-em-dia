package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Equipe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
    List<Equipe> findAllByOrderByNome();
    boolean existsByNomeIgnoreCase(String nome);
}
