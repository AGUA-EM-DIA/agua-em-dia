package br.com.aguaemdia.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@DynamicUpdate
@Table(name = "Localizacao")
public class Localizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false, precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(name = "endereco", nullable = true, length = 300)
    private String endereco;

    @Column(name = "bairro_id", nullable = true)
    private Long bairroId;

    @Column(name = "origem_localizacao", nullable = false, length = 10)
    private String origemLocalizacao;

    @Column(name = "registrada_em", nullable = false)
    private OffsetDateTime registradaEm;

    @PrePersist
    void aoPersistir() {
        if (registradaEm == null) registradaEm = OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }

    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public Long getBairroId() { return bairroId; }
    public void setBairroId(Long bairroId) { this.bairroId = bairroId; }

    public String getOrigemLocalizacao() { return origemLocalizacao; }
    public void setOrigemLocalizacao(String origemLocalizacao) { this.origemLocalizacao = origemLocalizacao; }

    public OffsetDateTime getRegistradaEm() { return registradaEm; }
    public void setRegistradaEm(OffsetDateTime registradaEm) { this.registradaEm = registradaEm; }
}
