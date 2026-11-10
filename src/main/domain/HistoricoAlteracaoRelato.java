package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "HistoricoAlteracaoRelato")
public class HistoricoAlteracaoRelato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "relato_id", nullable = false)
    private Long relatoId;

    @Column(name = "campo", nullable = false, length = 40)
    private String campo;

    @Column(name = "valor_anterior", nullable = true, length = 2000)
    private String valorAnterior;

    @Column(name = "valor_novo", nullable = true, length = 2000)
    private String valorNovo;

    @Column(name = "alterado_por", nullable = false)
    private Long alteradoPor;

    @Column(name = "alterado_em", nullable = false)
    private OffsetDateTime alteradoEm;

    @Column(name = "motivo", nullable = true, length = 500)
    private String motivo;

    @PrePersist
    void aoPersistir() {
        if (alteradoEm == null) alteradoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRelatoId() { return relatoId; }
    public void setRelatoId(Long relatoId) { this.relatoId = relatoId; }

    public String getCampo() { return campo; }
    public void setCampo(String campo) { this.campo = campo; }

    public String getValorAnterior() { return valorAnterior; }
    public void setValorAnterior(String valorAnterior) { this.valorAnterior = valorAnterior; }

    public String getValorNovo() { return valorNovo; }
    public void setValorNovo(String valorNovo) { this.valorNovo = valorNovo; }

    public Long getAlteradoPor() { return alteradoPor; }
    public void setAlteradoPor(Long alteradoPor) { this.alteradoPor = alteradoPor; }

    public OffsetDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(OffsetDateTime alteradoEm) { this.alteradoEm = alteradoEm; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
