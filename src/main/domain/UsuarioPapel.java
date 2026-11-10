package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "UsuarioPapel")
public class UsuarioPapel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "papel_id", nullable = false)
    private Long papelId;

    @Column(name = "atribuido_em", nullable = false)
    private OffsetDateTime atribuidoEm;

    @Column(name = "atribuido_por", nullable = true)
    private Long atribuidoPor;

    @Column(name = "revogado_em", nullable = true)
    private OffsetDateTime revogadoEm;

    @PrePersist
    void aoPersistir() {
        if (atribuidoEm == null) atribuidoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getPapelId() { return papelId; }
    public void setPapelId(Long papelId) { this.papelId = papelId; }

    public OffsetDateTime getAtribuidoEm() { return atribuidoEm; }
    public void setAtribuidoEm(OffsetDateTime atribuidoEm) { this.atribuidoEm = atribuidoEm; }

    public Long getAtribuidoPor() { return atribuidoPor; }
    public void setAtribuidoPor(Long atribuidoPor) { this.atribuidoPor = atribuidoPor; }

    public OffsetDateTime getRevogadoEm() { return revogadoEm; }
    public void setRevogadoEm(OffsetDateTime revogadoEm) { this.revogadoEm = revogadoEm; }
}
