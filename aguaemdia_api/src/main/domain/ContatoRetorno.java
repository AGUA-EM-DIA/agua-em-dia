package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "ContatoRetorno")
public class ContatoRetorno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "avaliacao_id", nullable = false)
    private Long avaliacaoId;

    @Column(name = "responsavel_id", nullable = true)
    private Long responsavelId;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao = "ABERTO";

    @Column(name = "observacao", nullable = true, length = 2000)
    private String observacao;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "encerrado_em", nullable = true)
    private OffsetDateTime encerradoEm;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAvaliacaoId() { return avaliacaoId; }
    public void setAvaliacaoId(Long avaliacaoId) { this.avaliacaoId = avaliacaoId; }

    public Long getResponsavelId() { return responsavelId; }
    public void setResponsavelId(Long responsavelId) { this.responsavelId = responsavelId; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public OffsetDateTime getEncerradoEm() { return encerradoEm; }
    public void setEncerradoEm(OffsetDateTime encerradoEm) { this.encerradoEm = encerradoEm; }
}
