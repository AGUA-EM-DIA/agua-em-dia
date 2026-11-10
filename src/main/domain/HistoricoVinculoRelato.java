package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "HistoricoVinculoRelato")
public class HistoricoVinculoRelato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "relato_id", nullable = false)
    private Long relatoId;

    @Column(name = "vazamento_anterior_id", nullable = false)
    private Long vazamentoAnteriorId;

    @Column(name = "vazamento_novo_id", nullable = false)
    private Long vazamentoNovoId;

    @Column(name = "vinculo_id", nullable = true)
    private Long vinculoId;

    @Column(name = "alterado_por", nullable = false)
    private Long alteradoPor;

    @Column(name = "alterado_em", nullable = false)
    private OffsetDateTime alteradoEm;

    @Column(name = "motivo", nullable = true, length = 1000)
    private String motivo;

    @PrePersist
    void aoPersistir() {
        if (alteradoEm == null) alteradoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRelatoId() { return relatoId; }
    public void setRelatoId(Long relatoId) { this.relatoId = relatoId; }

    public Long getVazamentoAnteriorId() { return vazamentoAnteriorId; }
    public void setVazamentoAnteriorId(Long vazamentoAnteriorId) { this.vazamentoAnteriorId = vazamentoAnteriorId; }

    public Long getVazamentoNovoId() { return vazamentoNovoId; }
    public void setVazamentoNovoId(Long vazamentoNovoId) { this.vazamentoNovoId = vazamentoNovoId; }

    public Long getVinculoId() { return vinculoId; }
    public void setVinculoId(Long vinculoId) { this.vinculoId = vinculoId; }

    public Long getAlteradoPor() { return alteradoPor; }
    public void setAlteradoPor(Long alteradoPor) { this.alteradoPor = alteradoPor; }

    public OffsetDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(OffsetDateTime alteradoEm) { this.alteradoEm = alteradoEm; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
