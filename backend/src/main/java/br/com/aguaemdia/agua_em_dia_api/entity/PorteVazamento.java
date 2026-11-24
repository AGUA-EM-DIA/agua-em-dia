package br.com.aguaemdia.agua_em_dia_api.entity;

/** Tamanho do vazamento, com a estimativa de perda em litros por hora (mesmos valores do protótipo). */
public enum PorteVazamento {
    GOTEJAMENTO(3),
    FIO(60),
    MEDIO(600),
    FORTE(2400),
    JORRO(9000);

    private final int litrosHora;

    PorteVazamento(int litrosHora) {
        this.litrosHora = litrosHora;
    }

    public int getLitrosHora() {
        return litrosHora;
    }
}
