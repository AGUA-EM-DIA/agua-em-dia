package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;

@Entity
@DynamicUpdate
@Table(name = "PorteVazamento")
public class PorteVazamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 30)
    private String codigo;

    @Column(name = "nome", nullable = false, length = 60)
    private String nome;

    @Column(name = "ordem", nullable = false)
    private Integer ordem;

    @Column(name = "vazao_base_l_h", nullable = false, precision = 18, scale = 4)
    private BigDecimal vazaoBaseLH;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public BigDecimal getVazaoBaseLH() { return vazaoBaseLH; }
    public void setVazaoBaseLH(BigDecimal vazaoBaseLH) { this.vazaoBaseLH = vazaoBaseLH; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
