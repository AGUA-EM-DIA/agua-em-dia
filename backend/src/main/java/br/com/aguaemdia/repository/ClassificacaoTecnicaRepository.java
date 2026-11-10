package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.ClassificacaoTecnica;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassificacaoTecnicaRepository extends JpaRepository<ClassificacaoTecnica, Long> {
    Optional<ClassificacaoTecnica> findByVazamentoIdAndVigenteAteIsNull(Long vazamentoId);
    List<ClassificacaoTecnica> findByVazamentoIdOrderByVigenteDesdeDesc(Long vazamentoId);
}
