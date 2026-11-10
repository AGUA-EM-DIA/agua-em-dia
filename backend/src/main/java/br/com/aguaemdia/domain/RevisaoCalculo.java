package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "RevisaoCalculo")
public class RevisaoCalculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "motivo", nullable = false, length = 300)
    private String motivo;

    @Column(name = "versao_anterior", nullable = false)
    private Integer versaoAnterior;

    @Column(name = "versao_nova", nullable = false)
    private Integer versaoNova;

    @Column(name = "executado_em", nullable = false)
    private OffsetDateTime executadoEm;

    @Column(name = "executado_por", nullable = true)
    private Long executadoPor;

    @PrePersist
    void aoPersistir() {
        if (executadoEm == null) executadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getVersaoAnterior() { return versaoAnterior; }
    public void setVersaoAnterior(Integer versaoAnterior) { this.versaoAnterior = versaoAnterior; }

    public Integer getVersaoNova() { return versaoNova; }
    public void setVersaoNova(Integer versaoNova) { this.versaoNova = versaoNova; }

    public OffsetDateTime getExecutadoEm() { return executadoEm; }
    public void setExecutadoEm(OffsetDateTime executadoEm) { this.executadoEm = executadoEm; }

    public Long getExecutadoPor() { return executadoPor; }
    public void setExecutadoPor(Long executadoPor) { this.executadoPor = executadoPor; }
}
