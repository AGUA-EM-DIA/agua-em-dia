package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "Atendimento")
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vazamento_id", nullable = false)
    private Long vazamentoId;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "AGUARDANDO_EQUIPE";

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "iniciado_em", nullable = true)
    private OffsetDateTime iniciadoEm;

    @Column(name = "encerrado_em", nullable = true)
    private OffsetDateTime encerradoEm;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVazamentoId() { return vazamentoId; }
    public void setVazamentoId(Long vazamentoId) { this.vazamentoId = vazamentoId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public OffsetDateTime getIniciadoEm() { return iniciadoEm; }
    public void setIniciadoEm(OffsetDateTime iniciadoEm) { this.iniciadoEm = iniciadoEm; }

    public OffsetDateTime getEncerradoEm() { return encerradoEm; }
    public void setEncerradoEm(OffsetDateTime encerradoEm) { this.encerradoEm = encerradoEm; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}
