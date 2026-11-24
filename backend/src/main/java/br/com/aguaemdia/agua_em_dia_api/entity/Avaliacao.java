package br.com.aguaemdia.agua_em_dia_api.entity;

import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/** Avaliação do atendimento: nota de 1 a 5 e comentário opcional. Uma por vazamento. */
@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vazamento_id", nullable = false, unique = true)
    private Vazamento vazamento;

    @Column(nullable = false)
    private Integer nota;

    @Column(length = 500)
    private String comentario;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    void aoCriar() {
        criadoEm = Tempo.agora();
    }

    public Avaliacao() {
    }

    public Avaliacao(Vazamento vazamento, Integer nota, String comentario) {
        this.vazamento = vazamento;
        this.nota = nota;
        this.comentario = comentario;
    }

    public Long getId() {
        return id;
    }

    public Vazamento getVazamento() {
        return vazamento;
    }

    public Integer getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
}
