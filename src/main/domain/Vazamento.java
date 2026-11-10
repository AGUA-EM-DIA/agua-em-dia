package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "Vazamento")
public class Vazamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "categoria_id", nullable = false)
    private Long categoriaId;

    @Column(name = "localizacao_atual_id", nullable = false)
    private Long localizacaoAtualId;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "PROVISORIA";

    @Column(name = "primeiro_relato_valido_em", nullable = false)
    private OffsetDateTime primeiroRelatoValidoEm;

    @Column(name = "prazo_inicio_em", nullable = false)
    private OffsetDateTime prazoInicioEm;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "resolvido_em", nullable = true)
    private OffsetDateTime resolvidoEm;

    @Column(name = "prazo_efetivo_horas", nullable = true)
    private Integer prazoEfetivoHoras;

    @Column(name = "matriz_prazo_id", nullable = true)
    private Long matrizPrazoId;

    @Column(name = "vazao_incerta", nullable = false)
    private Boolean vazaoIncerta = Boolean.FALSE;

    @Column(name = "prioridade_atual", nullable = true, precision = 18, scale = 6)
    private BigDecimal prioridadeAtual;

    @Column(name = "prioridade_calculada_em", nullable = true)
    private OffsetDateTime prioridadeCalculadaEm;

    @Version
    @Column(name = "versao", nullable = false)
    private Long versao;

    @PrePersist
    void aoPersistir() {
        if (criadoEm == null) criadoEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }

    public Long getLocalizacaoAtualId() { return localizacaoAtualId; }
    public void setLocalizacaoAtualId(Long localizacaoAtualId) { this.localizacaoAtualId = localizacaoAtualId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getPrimeiroRelatoValidoEm() { return primeiroRelatoValidoEm; }
    public void setPrimeiroRelatoValidoEm(OffsetDateTime primeiroRelatoValidoEm) { this.primeiroRelatoValidoEm = primeiroRelatoValidoEm; }

    public OffsetDateTime getPrazoInicioEm() { return prazoInicioEm; }
    public void setPrazoInicioEm(OffsetDateTime prazoInicioEm) { this.prazoInicioEm = prazoInicioEm; }

    public OffsetDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(OffsetDateTime criadoEm) { this.criadoEm = criadoEm; }

    public OffsetDateTime getResolvidoEm() { return resolvidoEm; }
    public void setResolvidoEm(OffsetDateTime resolvidoEm) { this.resolvidoEm = resolvidoEm; }

    public Integer getPrazoEfetivoHoras() { return prazoEfetivoHoras; }
    public void setPrazoEfetivoHoras(Integer prazoEfetivoHoras) { this.prazoEfetivoHoras = prazoEfetivoHoras; }

    public Long getMatrizPrazoId() { return matrizPrazoId; }
    public void setMatrizPrazoId(Long matrizPrazoId) { this.matrizPrazoId = matrizPrazoId; }

    public Boolean getVazaoIncerta() { return vazaoIncerta; }
    public void setVazaoIncerta(Boolean vazaoIncerta) { this.vazaoIncerta = vazaoIncerta; }

    public BigDecimal getPrioridadeAtual() { return prioridadeAtual; }
    public void setPrioridadeAtual(BigDecimal prioridadeAtual) { this.prioridadeAtual = prioridadeAtual; }

    public OffsetDateTime getPrioridadeCalculadaEm() { return prioridadeCalculadaEm; }
    public void setPrioridadeCalculadaEm(OffsetDateTime prioridadeCalculadaEm) { this.prioridadeCalculadaEm = prioridadeCalculadaEm; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}
