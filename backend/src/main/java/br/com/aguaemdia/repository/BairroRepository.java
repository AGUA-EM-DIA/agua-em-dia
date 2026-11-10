package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Bairro;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BairroRepository extends JpaRepository<Bairro, Long> {
    Optional<Bairro> findByNomeNormalizado(String nomeNormalizado);
}
