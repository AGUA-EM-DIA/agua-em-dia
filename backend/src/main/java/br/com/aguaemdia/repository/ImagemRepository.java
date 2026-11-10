package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Imagem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemRepository extends JpaRepository<Imagem, Long> {
    List<Imagem> findByRelatoId(Long relatoId);
    List<Imagem> findByAtendimentoId(Long atendimentoId);
    long countByRelatoId(Long relatoId);
    long countByAtendimentoId(Long atendimentoId);
}
