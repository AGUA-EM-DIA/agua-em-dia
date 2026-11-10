package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Papel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PapelRepository extends JpaRepository<Papel, Long> {
    Optional<Papel> findByNome(String nome);
}
