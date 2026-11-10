package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.CategoriaOcorrencia;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaOcorrenciaRepository extends JpaRepository<CategoriaOcorrencia, Long> {
    Optional<CategoriaOcorrencia> findByCodigo(String codigo);
    List<CategoriaOcorrencia> findByAtivoTrueOrderByNome();
}
