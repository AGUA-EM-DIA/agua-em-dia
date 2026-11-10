package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "HistoricoStatus")
public class HistoricoStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidade_tipo", nullable = false, length = 40)
    private String entidadeTipo;

    @Column(name = "entidade_id", nullable = false)
    private Long entidadeId;

    @Column(name = "status_anterior", nullable = true, length = 30)
    private String statusAnterior;

    @Column(name = "status_novo", nullable = false, length = 30)
    private String statusNovo;

    @Column(name = "alterado_por", nullable = true)
    private Long alteradoPor;

    @Column(name = "alterado_em", nullable = false)
    private OffsetDateTime alteradoEm;

    @Column(name = "justificativa", nullable = true, length = 1000)
    private String justificativa;

    @PrePersist
    void aoPersistir() {
        if (alteradoEm == null) alteradoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntidadeTipo() { return entidadeTipo; }
    public void setEntidadeTipo(String entidadeTipo) { this.entidadeTipo = entidadeTipo; }

    public Long getEntidadeId() { return entidadeId; }
    public void setEntidadeId(Long entidadeId) { this.entidadeId = entidadeId; }

    public String getStatusAnterior() { return statusAnterior; }
    public void setStatusAnterior(String statusAnterior) { this.statusAnterior = statusAnterior; }

    public String getStatusNovo() { return statusNovo; }
    public void setStatusNovo(String statusNovo) { this.statusNovo = statusNovo; }

    public Long getAlteradoPor() { return alteradoPor; }
    public void setAlteradoPor(Long alteradoPor) { this.alteradoPor = alteradoPor; }

    public OffsetDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(OffsetDateTime alteradoEm) { this.alteradoEm = alteradoEm; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
}
