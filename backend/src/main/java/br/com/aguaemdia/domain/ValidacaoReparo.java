package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "ValidacaoReparo")
public class ValidacaoReparo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conclusao_id", nullable = false)
    private Long conclusaoId;

    @Column(name = "operador_id", nullable = false)
    private Long operadorId;

    @Column(name = "decisao", nullable = false, length = 20)
    private String decisao;

    @Column(name = "justificativa", nullable = true, length = 1000)
    private String justificativa;

    @Column(name = "validado_em", nullable = false)
    private OffsetDateTime validadoEm;

    @PrePersist
    void aoPersistir() {
        if (validadoEm == null) validadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConclusaoId() { return conclusaoId; }
    public void setConclusaoId(Long conclusaoId) { this.conclusaoId = conclusaoId; }

    public Long getOperadorId() { return operadorId; }
    public void setOperadorId(Long operadorId) { this.operadorId = operadorId; }

    public String getDecisao() { return decisao; }
    public void setDecisao(String decisao) { this.decisao = decisao; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public OffsetDateTime getValidadoEm() { return validadoEm; }
    public void setValidadoEm(OffsetDateTime validadoEm) { this.validadoEm = validadoEm; }
}
