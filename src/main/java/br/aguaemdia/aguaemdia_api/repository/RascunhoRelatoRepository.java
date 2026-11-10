package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.RascunhoRelato;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RascunhoRelatoRepository extends JpaRepository<RascunhoRelato, Long> {
    List<RascunhoRelato> findByUsuarioIdOrderByAtualizadoEmDesc(Long usuarioId);
    Optional<RascunhoRelato> findByIdAndUsuarioId(Long id, Long usuarioId);
}
