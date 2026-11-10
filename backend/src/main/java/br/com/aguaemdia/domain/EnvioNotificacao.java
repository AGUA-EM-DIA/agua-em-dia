package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.time.OffsetDateTime;

@Entity
@DynamicUpdate
@Table(name = "EnvioNotificacao")
public class EnvioNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notificacao_id", nullable = false)
    private Long notificacaoId;

    @Column(name = "canal", nullable = false, length = 10)
    private String canal;

    @Column(name = "situacao", nullable = false, length = 20)
    private String situacao = "PENDENTE";

    @Column(name = "tentativas", nullable = false)
    private Integer tentativas = 0;

    @Column(name = "enviado_em", nullable = true)
    private OffsetDateTime enviadoEm;

    @Column(name = "erro", nullable = true, length = 500)
    private String erro;

    @Column(name = "proxima_tentativa_em", nullable = true)
    private OffsetDateTime proximaTentativaEm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNotificacaoId() { return notificacaoId; }
    public void setNotificacaoId(Long notificacaoId) { this.notificacaoId = notificacaoId; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public Integer getTentativas() { return tentativas; }
    public void setTentativas(Integer tentativas) { this.tentativas = tentativas; }

    public OffsetDateTime getEnviadoEm() { return enviadoEm; }
    public void setEnviadoEm(OffsetDateTime enviadoEm) { this.enviadoEm = enviadoEm; }

    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }

    public OffsetDateTime getProximaTentativaEm() { return proximaTentativaEm; }
    public void setProximaTentativaEm(OffsetDateTime proximaTentativaEm) { this.proximaTentativaEm = proximaTentativaEm; }
}
