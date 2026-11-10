package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "HistoricoPrioridade")
public class HistoricoPrioridade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "pontuacao", nullable = true, precision = 18, scale = 6)
    private BigDecimal pontuacao;

    @Column(name = "parametros_utilizados", nullable = false, length = Integer.MAX_VALUE)
    private String parametrosUtilizados;

    @Column(name = "calculado_em", nullable = false)
    private OffsetDateTime calculadoEm;

    @PrePersist
    void aoPersistir() {
        if (calculadoEm == null) calculadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public BigDecimal getPontuacao() { return pontuacao; }
    public void setPontuacao(BigDecimal pontuacao) { this.pontuacao = pontuacao; }

    public String getParametrosUtilizados() { return parametrosUtilizados; }
    public void setParametrosUtilizados(String parametrosUtilizados) { this.parametrosUtilizados = parametrosUtilizados; }

    public OffsetDateTime getCalculadoEm() { return calculadoEm; }
    public void setCalculadoEm(OffsetDateTime calculadoEm) { this.calculadoEm = calculadoEm; }
}
