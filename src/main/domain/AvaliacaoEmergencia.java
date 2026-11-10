package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "AvaliacaoEmergencia")
public class AvaliacaoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "sinalizacao_id", nullable = true)
    private Long sinalizacaoId;

    @Column(name = "operador_id", nullable = false)
    private Long operadorId;

    @Column(name = "decisao", nullable = false, length = 20)
    private String decisao;

    @Column(name = "gravidade", nullable = true)
    private Short gravidade;

    @Column(name = "justificativa", nullable = false, length = 1000)
    private String justificativa;

    @Column(name = "avaliado_em", nullable = false)
    private OffsetDateTime avaliadoEm;

    @PrePersist
    void aoPersistir() {
        if (avaliadoEm == null) avaliadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public Long getSinalizacaoId() { return sinalizacaoId; }
    public void setSinalizacaoId(Long sinalizacaoId) { this.sinalizacaoId = sinalizacaoId; }

    public Long getOperadorId() { return operadorId; }
    public void setOperadorId(Long operadorId) { this.operadorId = operadorId; }

    public String getDecisao() { return decisao; }
    public void setDecisao(String decisao) { this.decisao = decisao; }

    public Short getGravidade() { return gravidade; }
    public void setGravidade(Short gravidade) { this.gravidade = gravidade; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public OffsetDateTime getAvaliadoEm() { return avaliadoEm; }
    public void setAvaliadoEm(OffsetDateTime avaliadoEm) { this.avaliadoEm = avaliadoEm; }
}
