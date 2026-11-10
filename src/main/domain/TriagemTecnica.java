package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "TriagemTecnica")
public class TriagemTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao = "ABERTA";

    @Column(name = "motivo", nullable = false, length = 500)
    private String motivo;

    @Column(name = "responsavel_id", nullable = true)
    private Long responsavelId;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "concluido_em", nullable = true)
    private OffsetDateTime concluidoEm;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public OffsetDateTime getConcluidoEm() { return concluidoEm; }
    public void setConcluidoEm(OffsetDateTime concluidoEm) { this.concluidoEm = concluidoEm; }
}
