package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "VinculoDuplicidade")
public class VinculoDuplicidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origem_id", nullable = false)
    private Long origemId;

    @Column(name = "canonico_id", nullable = false)
    private Long canonicoId;

    @Column(name = "associado_por", nullable = false)
    private Long associadoPor;

    @Column(name = "associado_em", nullable = false)
    private OffsetDateTime associadoEm;

    @Column(name = "justificativa", nullable = false, length = 1000)
    private String justificativa;

    @Column(name = "revertido_por", nullable = true)
    private Long revertidoPor;

    @Column(name = "revertido_em", nullable = true)
    private OffsetDateTime revertidoEm;

    @Column(name = "motivo_reversao", nullable = true, length = 1000)
    private String motivoReversao;

    @PrePersist
    void aoPersistir() {
        if (associadoEm == null) associadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrigemId() { return origemId; }
    public void setOrigemId(Long origemId) { this.origemId = origemId; }

    public Long getCanonicoId() { return canonicoId; }
    public void setCanonicoId(Long canonicoId) { this.canonicoId = canonicoId; }

    public Long getAssociadoPor() { return associadoPor; }
    public void setAssociadoPor(Long associadoPor) { this.associadoPor = associadoPor; }

    public OffsetDateTime getAssociadoEm() { return associadoEm; }
    public void setAssociadoEm(OffsetDateTime associadoEm) { this.associadoEm = associadoEm; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public Long getRevertidoPor() { return revertidoPor; }
    public void setRevertidoPor(Long revertidoPor) { this.revertidoPor = revertidoPor; }

    public OffsetDateTime getRevertidoEm() { return revertidoEm; }
    public void setRevertidoEm(OffsetDateTime revertidoEm) { this.revertidoEm = revertidoEm; }

    public String getMotivoReversao() { return motivoReversao; }
    public void setMotivoReversao(String motivoReversao) { this.motivoReversao = motivoReversao; }
}
