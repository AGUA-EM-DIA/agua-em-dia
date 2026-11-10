package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "ConclusaoServico")
public class ConclusaoServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "atendimento_id", nullable = false)
    private Long atendimentoId;

    @Column(name = "integrante_id", nullable = false)
    private Long integranteId;

    @Column(name = "resumo", nullable = false, length = 4000)
    private String resumo;

    @Column(name = "reparo_efetivo_em", nullable = false)
    private OffsetDateTime reparoEfetivoEm;

    @Column(name = "informado_em", nullable = false)
    private OffsetDateTime informadoEm;

    @PrePersist
    void aoPersistir() {
        if (informadoEm == null) informadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAtendimentoId() { return atendimentoId; }
    public void setAtendimentoId(Long atendimentoId) { this.atendimentoId = atendimentoId; }

    public Long getIntegranteId() { return integranteId; }
    public void setIntegranteId(Long integranteId) { this.integranteId = integranteId; }

    public String getResumo() { return resumo; }
    public void setResumo(String resumo) { this.resumo = resumo; }

    public OffsetDateTime getReparoEfetivoEm() { return reparoEfetivoEm; }
    public void setReparoEfetivoEm(OffsetDateTime reparoEfetivoEm) { this.reparoEfetivoEm = reparoEfetivoEm; }

    public OffsetDateTime getInformadoEm() { return informadoEm; }
    public void setInformadoEm(OffsetDateTime informadoEm) { this.informadoEm = informadoEm; }
}
