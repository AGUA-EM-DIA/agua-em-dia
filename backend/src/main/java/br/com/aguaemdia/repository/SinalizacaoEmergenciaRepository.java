package br.com.aguaemdia.repository;

import br.com.aguaemdia.domain.SinalizacaoEmergencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SinalizacaoEmergenciaRepository extends JpaRepository<SinalizacaoEmergencia, Long> {

    /** Sinalizações ainda sem avaliação do operador (fila preventiva). */
    @Query("""
           select s from SinalizacaoEmergencia s
           where not exists (select 1 from AvaliacaoEmergencia a where a.sinalizacaoId = s.id)
           order by s.sinalizadoEm asc
           """)
    List<SinalizacaoEmergencia> semAvaliacao();
}
