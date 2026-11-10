package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;

@Entity
@DynamicUpdate
@Table(name = "MatrizPrazoAtendimento")
public class MatrizPrazoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "porte_id", nullable = false)
    private Long porteId;

    @Column(name = "infraestrutura_id", nullable = false)
    private Long infraestruturaId;

    @Column(name = "prazo_horas", nullable = false)
    private Integer prazoHoras;

    @Column(name = "vigente_desde", nullable = false)
    private OffsetDateTime vigenteDesde;

    @Column(name = "vigente_ate", nullable = true)
    private OffsetDateTime vigenteAte;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPorteId() { return porteId; }
    public void setPorteId(Long porteId) { this.porteId = porteId; }

    public Long getInfraestruturaId() { return infraestruturaId; }
    public void setInfraestruturaId(Long infraestruturaId) { this.infraestruturaId = infraestruturaId; }

    public Integer getPrazoHoras() { return prazoHoras; }
    public void setPrazoHoras(Integer prazoHoras) { this.prazoHoras = prazoHoras; }

    public OffsetDateTime getVigenteDesde() { return vigenteDesde; }
    public void setVigenteDesde(OffsetDateTime vigenteDesde) { this.vigenteDesde = vigenteDesde; }

    public OffsetDateTime getVigenteAte() { return vigenteAte; }
    public void setVigenteAte(OffsetDateTime vigenteAte) { this.vigenteAte = vigenteAte; }
}
