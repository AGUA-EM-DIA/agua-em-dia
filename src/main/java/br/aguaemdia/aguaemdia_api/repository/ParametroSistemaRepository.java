package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.ParametroSistema;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParametroSistemaRepository extends JpaRepository<ParametroSistema, Long> {
    Optional<ParametroSistema> findByChave(String chave);
    List<ParametroSistema> findAllByOrderByChave();
}
