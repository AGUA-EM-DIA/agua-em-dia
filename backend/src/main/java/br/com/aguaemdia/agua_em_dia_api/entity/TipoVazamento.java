package br.com.aguaemdia.agua_em_dia_api.entity;

/**
 * Tipo do vazamento. A perda por hora é o litros/hora do porte vezes o multiplicador do tipo.
 * A ligação clandestina não depende do porte: tem uma vazão fixa estimada por consumo.
 */
public enum TipoVazamento {
    ADUTORA(3.0, 4, 0),
    REDE(1.6, 24, 0),
    RAMAL(0.7, 72, 0),
    CLANDESTINA(0, 168, 800),
    DESPERDICIO(0.5, 48, 0);

    private final double multiplicador;
    private final int metaHoras;
    private final int vazaoFixa;

    TipoVazamento(double multiplicador, int metaHoras, int vazaoFixa) {
        this.multiplicador = multiplicador;
        this.metaHoras = metaHoras;
        this.vazaoFixa = vazaoFixa;
    }

    /** Prazo-meta, em horas, para o reparo. */
    public int getMetaHoras() {
        return metaHoras;
    }

    /** Estimativa de perda em litros por hora. */
    public int vazaoLitrosHora(PorteVazamento porte) {
        if (vazaoFixa > 0) {
            return vazaoFixa;
        }
        return (int) Math.round(porte.getLitrosHora() * multiplicador);
    }
}
