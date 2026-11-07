package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "Auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = true)
    private Long usuarioId;

    @Column(name = "acao", nullable = false, length = 60)
    private String acao;

    @Column(name = "entidade_tipo", nullable = false, length = 40)
    private String entidadeTipo;

    @Column(name = "entidade_id", nullable = true)
    private Long entidadeId;

    @Column(name = "dados_anteriores", nullable = true, length = Integer.MAX_VALUE)
    private String dadosAnteriores;

    @Column(name = "dados_novos", nullable = true, length = Integer.MAX_VALUE)
    private String dadosNovos;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }

    public String getEntidadeTipo() { return entidadeTipo; }
    public void setEntidadeTipo(String entidadeTipo) { this.entidadeTipo = entidadeTipo; }

    public Long getEntidadeId() { return entidadeId; }
    public void setEntidadeId(Long entidadeId) { this.entidadeId = entidadeId; }

    public String getDadosAnteriores() { return dadosAnteriores; }
    public void setDadosAnteriores(String dadosAnteriores) { this.dadosAnteriores = dadosAnteriores; }

    public String getDadosNovos() { return dadosNovos; }
    public void setDadosNovos(String dadosNovos) { this.dadosNovos = dadosNovos; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }
}
