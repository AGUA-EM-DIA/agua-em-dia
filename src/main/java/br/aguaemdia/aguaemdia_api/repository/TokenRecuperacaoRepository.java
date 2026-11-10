package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.TokenRecuperacao;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TokenRecuperacaoRepository extends JpaRepository<TokenRecuperacao, Long> {

    Optional<TokenRecuperacao> findByTokenHash(String tokenHash);

    @Modifying
    @Query("update TokenRecuperacao t set t.utilizadoEm = :agora where t.usuarioId = :usuarioId and t.utilizadoEm is null")
    int invalidarPendentes(@Param("usuarioId") Long usuarioId, @Param("agora") OffsetDateTime agora);
}
