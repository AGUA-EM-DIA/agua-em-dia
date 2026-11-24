package br.com.aguaemdia.agua_em_dia_api.entity;

import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

/** "Eu também vi": reforço de um cidadão a um vazamento já registrado. Cada pessoa reforça uma vez. */
@Entity
@Table(name = "apoio", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "vazamento_id"}))
public class Apoio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vazamento_id", nullable = false)
    private Vazamento vazamento;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Tempo.agora();
    }

    public Apoio() {
    }

    public Apoio(Usuario usuario, Vazamento vazamento) {
        this.usuario = usuario;
        this.vazamento = vazamento;
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Vazamento getVazamento() {
        return vazamento;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
