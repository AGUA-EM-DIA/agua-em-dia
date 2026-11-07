package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.PorteVazamento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PorteVazamentoRepository extends JpaRepository<PorteVazamento, Long> {
    Optional<PorteVazamento> findByCodigo(String codigo);
    List<PorteVazamento> findByAtivoTrueOrderByOrdem();
    List<PorteVazamento> findAllByOrderByOrdem();
}
