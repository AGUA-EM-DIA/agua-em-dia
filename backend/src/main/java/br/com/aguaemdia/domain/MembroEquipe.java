package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "MembroEquipe")
public class MembroEquipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "equipe_id", nullable = false)
    private Long equipeId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "inicio_vinculo", nullable = false)
    private OffsetDateTime inicioVinculo;

    @Column(name = "fim_vinculo", nullable = true)
    private OffsetDateTime fimVinculo;

    @PrePersist
    void aoPersistir() {
        if (inicioVinculo == null) inicioVinculo = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEquipeId() { return equipeId; }
    public void setEquipeId(Long equipeId) { this.equipeId = equipeId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public OffsetDateTime getInicioVinculo() { return inicioVinculo; }
    public void setInicioVinculo(OffsetDateTime inicioVinculo) { this.inicioVinculo = inicioVinculo; }

    public OffsetDateTime getFimVinculo() { return fimVinculo; }
    public void setFimVinculo(OffsetDateTime fimVinculo) { this.fimVinculo = fimVinculo; }
}
