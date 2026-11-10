package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "SolicitacaoReabertura")
public class SolicitacaoReabertura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "solicitante_id", nullable = false)
    private Long solicitanteId;

    @Column(name = "motivo", nullable = false, length = 1000)
    private String motivo;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao = "PENDENTE";

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "decidido_por", nullable = true)
    private Long decididoPor;

    @Column(name = "decidido_em", nullable = true)
    private OffsetDateTime decididoEm;

    @Column(name = "decisao_justificativa", nullable = true, length = 1000)
    private String decisaoJustificativa;

    @Column(name = "atendimento_novo_id", nullable = true)
    private Long atendimentoNovoId;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public Long getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Long solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public Long getDecididoPor() { return decididoPor; }
    public void setDecididoPor(Long decididoPor) { this.decididoPor = decididoPor; }

    public OffsetDateTime getDecididoEm() { return decididoEm; }
    public void setDecididoEm(OffsetDateTime decididoEm) { this.decididoEm = decididoEm; }

    public String getDecisaoJustificativa() { return decisaoJustificativa; }
    public void setDecisaoJustificativa(String decisaoJustificativa) { this.decisaoJustificativa = decisaoJustificativa; }

    public Long getAtendimentoNovoId() { return atendimentoNovoId; }
    public void setAtendimentoNovoId(Long atendimentoNovoId) { this.atendimentoNovoId = atendimentoNovoId; }
}
