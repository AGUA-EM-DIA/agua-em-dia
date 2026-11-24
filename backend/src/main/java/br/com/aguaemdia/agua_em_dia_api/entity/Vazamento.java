package br.com.aguaemdia.agua_em_dia_api.entity;

import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

/** Vazamento registrado por um cidadão. */
@Entity
@Table(name = "vazamento")
public class Vazamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoVazamento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PorteVazamento porte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SituacaoVazamento situacao;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 255)
    private String endereco;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "despachado_em")
    private LocalDateTime despachadoEm;

    @Column(name = "concluido_em")
    private LocalDateTime concluidoEm;

    /** Verdadeiro quando a situação mudou e o autor ainda não viu (alimenta o aviso do app). */
    @Column(name = "novidade_nao_vista", nullable = false)
    private boolean novidadeNaoVista;

    @PrePersist
    void aoCriar() {
        if (criadoEm == null) {
            criadoEm = Tempo.agora();
        }
        if (situacao == null) {
            situacao = SituacaoVazamento.ABERTO;
        }
    }

    /** Protocolo mostrado ao cidadão, calculado a partir do ano e do id (não é gravado no banco). */
    public String protocolo() {
        return "RE-" + criadoEm.getYear() + "-" + String.format("%04d", id);
    }

    /** Estimativa de perda em litros por hora. */
    public int vazaoLitrosHora() {
        return tipo.vazaoLitrosHora(porte);
    }

    /** Horas desde o registro até a conclusão (ou até agora, se ainda não foi resolvido). */
    public double horasEmAberto(LocalDateTime agora) {
        LocalDateTime fim = (situacao == SituacaoVazamento.RESOLVIDO && concluidoEm != null) ? concluidoEm : agora;
        return Duration.between(criadoEm, fim).toMillis() / 3_600_000.0;
    }

    /** Estimativa de água perdida até agora, em litros. */
    public long litrosPerdidos(LocalDateTime agora) {
        return Math.round(vazaoLitrosHora() * horasEmAberto(agora));
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoVazamento getTipo() {
        return tipo;
    }

    public void setTipo(TipoVazamento tipo) {
        this.tipo = tipo;
    }

    public PorteVazamento getPorte() {
        return porte;
    }

    public void setPorte(PorteVazamento porte) {
        this.porte = porte;
    }

    public SituacaoVazamento getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoVazamento situacao) {
        this.situacao = situacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public LocalDateTime getDespachadoEm() {
        return despachadoEm;
    }

    public void setDespachadoEm(LocalDateTime despachadoEm) {
        this.despachadoEm = despachadoEm;
    }

    public LocalDateTime getConcluidoEm() {
        return concluidoEm;
    }

    public void setConcluidoEm(LocalDateTime concluidoEm) {
        this.concluidoEm = concluidoEm;
    }

    public boolean isNovidadeNaoVista() {
        return novidadeNaoVista;
    }

    public void setNovidadeNaoVista(boolean novidadeNaoVista) {
        this.novidadeNaoVista = novidadeNaoVista;
    }
}
