package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "AtribuicaoEquipe")
public class AtribuicaoEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "atendimento_id", nullable = false)
    private Long atendimentoId;

    @Column(name = "equipe_id", nullable = false)
    private Long equipeId;

    @Column(name = "atribuido_por", nullable = false)
    private Long atribuidoPor;

    @Column(name = "atribuido_em", nullable = false)
    private OffsetDateTime atribuidoEm;

    @Column(name = "encerrado_em", nullable = true)
    private OffsetDateTime encerradoEm;

    @Column(name = "motivo_substituicao", nullable = true, length = 500)
    private String motivoSubstituicao;

    @PrePersist
    void aoPersistir() {
        if (atribuidoEm == null) atribuidoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(Long atendimentoId) { this.atendimentoId = atendimentoId; }

    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public Long getAtribuidoPor() { return atribuidoPor; }
    public void setAtribuidoPor(Long atribuidoPor) { this.atribuidoPor = atribuidoPor; }

    public OffsetDateTime getAtribuidoEm() { return atribuidoEm; }
    public void setAtribuidoEm(OffsetDateTime atribuidoEm) { this.atribuidoEm = atribuidoEm; }

    public OffsetDateTime getEncerradoEm() { return encerradoEm; }
    public void setEncerradoEm(OffsetDateTime encerradoEm) { this.encerradoEm = encerradoEm; }

    public String getMotivoSubstituicao() { return motivoSubstituicao; }
    public void setMotivoSubstituicao(String motivoSubstituicao) { this.motivoSubstituicao = motivoSubstituicao; }
}
