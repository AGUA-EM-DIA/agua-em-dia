package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "CancelamentoOcorrencia")
public class CancelamentoOcorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "responsavel_id", nullable = false)
    private Long responsavelId;

    @Column(name = "motivo", nullable = false, length = 1000)
    private String motivo;

    @Column(name = "cancelado_em", nullable = false)
    private OffsetDateTime canceladoEm;

    @PrePersist
    void aoPersistir() {
        if (canceladoEm == null) canceladoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public OffsetDateTime getCanceladoEm() { return canceladoEm; }
    public void setCanceladoEm(OffsetDateTime canceladoEm) { this.canceladoEm = canceladoEm; }
}
