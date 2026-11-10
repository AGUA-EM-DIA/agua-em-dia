package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
}
