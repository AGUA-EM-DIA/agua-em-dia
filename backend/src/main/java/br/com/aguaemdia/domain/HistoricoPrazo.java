package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "HistoricoPrazo")
public class HistoricoPrazo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "prazo_anterior", nullable = true)
    private Integer prazoAnterior;

    @Column(name = "prazo_novo", nullable = true)
    private Integer prazoNovo;

    @Column(name = "alterado_em", nullable = false)
    private OffsetDateTime alteradoEm;

    @Column(name = "motivo", nullable = false, length = 300)
    private String motivo;

    @PrePersist
    void aoPersistir() {
        if (alteradoEm == null) alteradoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public Integer getPrazoAnterior() { return prazoAnterior; }
    public void setPrazoAnterior(Integer prazoAnterior) { this.prazoAnterior = prazoAnterior; }

    public Integer getPrazoNovo() { return prazoNovo; }
    public void setPrazoNovo(Integer prazoNovo) { this.prazoNovo = prazoNovo; }

    public OffsetDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(OffsetDateTime alteradoEm) { this.alteradoEm = alteradoEm; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
