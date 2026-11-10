package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;

@Entity
@DynamicUpdate
@Table(name = "ClassificacaoTecnica")
public class ClassificacaoTecnica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "porte_id", nullable = false)
    private Long porteId;

    @Column(name = "infraestrutura_id", nullable = true)
    private Long infraestruturaId;

    @Column(name = "operador_id", nullable = false)
    private Long operadorId;

    @Column(name = "vigente_desde", nullable = false)
    private OffsetDateTime vigenteDesde;

    @Column(name = "vigente_ate", nullable = true)
    private OffsetDateTime vigenteAte;

    @Column(name = "motivo", nullable = true, length = 500)
    private String motivo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public Long getPorteId() { return porteId; }
    public void setPorteId(Long porteId) { this.porteId = porteId; }

    public Long getInfraestruturaId() { return infraestruturaId; }
    public void setInfraestruturaId(Long infraestruturaId) { this.infraestruturaId = infraestruturaId; }

    public Long getOperadorId() { return operadorId; }
    public void setOperadorId(Long operadorId) { this.operadorId = operadorId; }

    public OffsetDateTime getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(OffsetDateTime vigenteDesde) { this.vigenteDesde = vigenteDesde; }

    public OffsetDateTime getVigenteAte() { return vigenteAte; }
    public void setVigenteAte(OffsetDateTime vigenteAte) { this.vigenteAte = vigenteAte; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
