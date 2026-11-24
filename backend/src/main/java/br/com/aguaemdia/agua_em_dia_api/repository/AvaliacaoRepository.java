package br.com.aguaemdia.agua_em_dia_api.repository;

import br.com.aguaemdia.agua_em_dia_api.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    @Query("select a from Avaliacao a join fetch a.vazamento")
    List<Avaliacao> listarTodas();

    Optional<Avaliacao> findByVazamentoId(Long vazamentoId);

    boolean existsByVazamentoId(Long vazamentoId);

    long countByVazamentoUsuarioId(Long usuarioId);
}
