package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "AnaliseDuplicidade")
public class AnaliseDuplicidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "origem_id", nullable = false)
    private Long origemId;

    @Column(name = "candidato_id", nullable = false)
    private Long candidatoId;

    @Column(name = "distancia_metros", nullable = false, precision = 10, scale = 2)
    private BigDecimal distanciaMetros;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao = "SUGERIDA";

    @Column(name = "analisado_por", nullable = true)
    private Long analisadoPor;

    @Column(name = "analisado_em", nullable = true)
    private OffsetDateTime analisadoEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrigemId() { return origemId; }
    public void setOrigemId(Long origemId) { this.origemId = origemId; }

    public Long getCandidatoId() { return candidatoId; }
    public void setCandidatoId(Long candidatoId) { this.candidatoId = candidatoId; }

    public BigDecimal getDistanciaMetros() { return distanciaMetros; }
    public void setDistanciaMetros(BigDecimal distanciaMetros) { this.distanciaMetros = distanciaMetros; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public Long getAnalisadoPor() { return analisadoPor; }
    public void setAnalisadoPor(Long analisadoPor) { this.analisadoPor = analisadoPor; }

    public OffsetDateTime getAnalisadoEm() { return analisadoEm; }
    public void setAnalisadoEm(OffsetDateTime analisadoEm) { this.analisadoEm = analisadoEm; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
