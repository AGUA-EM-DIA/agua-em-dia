package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.EnvioNotificacao;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EnvioNotificacaoRepository extends JpaRepository<EnvioNotificacao, Long> {

    @Query("""
           select e from EnvioNotificacao e
           where e.canal = 'EMAIL' and e.situacao = 'PENDENTE'
             and (e.proximaTentativaEm is null or e.proximaTentativaEm <= :agora)
           order by e.id
           """)
    List<EnvioNotificacao> pendentes(@Param("agora") OffsetDateTime agora, Pageable pageable);
}
