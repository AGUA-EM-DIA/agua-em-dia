package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.TipoInfraestrutura;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoInfraestruturaRepository extends JpaRepository<TipoInfraestrutura, Long> {
    List<TipoInfraestrutura> findByAtivoTrueOrderByNome();
}
