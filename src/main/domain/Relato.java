package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "Relato")
public class Relato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "localizacao_original_id", nullable = false)
    private Long localizacaoOriginalId;

    @Column(name = "protocolo", nullable = false, length = 30)
    private String protocolo;

    @Column(name = "categoria_informada_id", nullable = false)
    private Long categoriaInformadaId;

    @Column(name = "porte_informado_id", nullable = false)
    private Long porteInformadoId;

    @Column(name = "infraestrutura_informada_id", nullable = true)
    private Long infraestruturaInformadaId;

    @Column(name = "descricao", nullable = true, length = 2000)
    private String descricao;

    @Column(name = "enviado_em", nullable = false)
    private OffsetDateTime enviadoEm;

    @PrePersist
    void aoPersistir() {
        if (enviadoEm == null) enviadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public Long getLocalizacaoOriginalId() { return localizacaoOriginalId; }
    public void setLocalizacaoOriginalId(Long localizacaoOriginalId) { this.localizacaoOriginalId = localizacaoOriginalId; }

    public String getProtocolo() { return protocolo; }
    public void setProtocolo(String protocolo) { this.protocolo = protocolo; }

    public Long getCategoriaInformadaId() { return categoriaInformadaId; }
    public void setCategoriaInformadaId(Long categoriaInformadaId) { this.categoriaInformadaId = categoriaInformadaId; }

    public Long getPorteInformadoId() { return porteInformadoId; }
    public void setPorteInformadoId(Long porteInformadoId) { this.porteInformadoId = porteInformadoId; }

    public Long getInfraestruturaInformadaId() { return infraestruturaInformadaId; }
    public void setInfraestruturaInformadaId(Long infraestruturaInformadaId) { this.infraestruturaInformadaId = infraestruturaInformadaId; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public OffsetDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(OffsetDateTime enviadoEm) { this.enviadoEm = enviadoEm; }
}
