package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "SolicitacaoCorrecao")
public class SolicitacaoCorrecao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "relato_id", nullable = false)
    private Long relatoId;

    @Column(name = "solicitante_id", nullable = false)
    private Long solicitanteId;

    @Column(name = "campo", nullable = false, length = 40)
    private String campo;

    @Column(name = "valor_solicitado", nullable = false, length = 2000)
    private String valorSolicitado;

    @Column(name = "justificativa", nullable = false, length = 1000)
    private String justificativa;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao = "PENDENTE";

    @Column(name = "analisado_por", nullable = true)
    private Long analisadoPor;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "analisado_em", nullable = true)
    private OffsetDateTime analisadoEm;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRelatoId() { return relatoId; }
    public void setRelatoId(Long relatoId) { this.relatoId = relatoId; }

    public Long getSolicitanteId() { return solicitanteId; }
    public void setSolicitanteId(Long solicitanteId) { this.solicitanteId = solicitanteId; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getValorSolicitado() { return valorSolicitado; }
    public void setValorSolicitado(String valorSolicitado) { this.valorSolicitado = valorSolicitado; }

    public String getJustificativa() { return justificativa; }
    public void setJustificativa(String justificativa) { this.justificativa = justificativa; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public Long getAnalisadoPor() { return analisadoPor; }
    public void setAnalisadoPor(Long analisadoPor) { this.analisadoPor = analisadoPor; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public OffsetDateTime getAnalisadoEm() { return analisadoEm; }
    public void setAnalisadoEm(OffsetDateTime analisadoEm) { this.analisadoEm = analisadoEm; }
}
