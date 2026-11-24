package br.com.aguaemdia.agua_em_dia_api.repository;

import br.com.aguaemdia.agua_em_dia_api.entity.RecuperacaoSenha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecuperacaoSenhaRepository extends JpaRepository<RecuperacaoSenha, Long> {

    /** Código mais recente que ainda não foi usado. */
    Optional<RecuperacaoSenha> findFirstByUsuarioIdAndUsadoEmIsNullOrderByCriadoEmDesc(Long usuarioId);

    List<RecuperacaoSenha> findByUsuarioIdAndUsadoEmIsNull(Long usuarioId);
}
