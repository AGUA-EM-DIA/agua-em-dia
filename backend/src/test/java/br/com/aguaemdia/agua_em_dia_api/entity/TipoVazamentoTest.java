package br.com.aguaemdia.agua_em_dia_api.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Confere a estimativa de perda com os mesmos valores do protótipo. */
class TipoVazamentoTest {

    @Test
    void portesTemOsLitrosPorHoraDefinidosNoProjeto() {
        assertEquals(3, PorteVazamento.GOTEJAMENTO.getLitrosHora());
        assertEquals(60, PorteVazamento.FIO.getLitrosHora());
        assertEquals(600, PorteVazamento.MEDIO.getLitrosHora());
        assertEquals(2400, PorteVazamento.FORTE.getLitrosHora());
        assertEquals(9000, PorteVazamento.JORRO.getLitrosHora());
    }

    @Test
    void vazaoEhPorteVezesMultiplicadorDoTipo() {
        assertEquals(1800, TipoVazamento.ADUTORA.vazaoLitrosHora(PorteVazamento.MEDIO));
        assertEquals(960, TipoVazamento.REDE.vazaoLitrosHora(PorteVazamento.MEDIO));
        assertEquals(420, TipoVazamento.RAMAL.vazaoLitrosHora(PorteVazamento.MEDIO));
        assertEquals(300, TipoVazamento.DESPERDICIO.vazaoLitrosHora(PorteVazamento.MEDIO));
        assertEquals(5, TipoVazamento.REDE.vazaoLitrosHora(PorteVazamento.GOTEJAMENTO));
        assertEquals(2, TipoVazamento.RAMAL.vazaoLitrosHora(PorteVazamento.GOTEJAMENTO));
    }

    @Test
    void ligacaoClandestinaTemVazaoFixa() {
        assertEquals(800, TipoVazamento.CLANDESTINA.vazaoLitrosHora(PorteVazamento.GOTEJAMENTO));
        assertEquals(800, TipoVazamento.CLANDESTINA.vazaoLitrosHora(PorteVazamento.JORRO));
    }

    @Test
    void metasDeReparoEmHoras() {
        assertEquals(4, TipoVazamento.ADUTORA.getMetaHoras());
        assertEquals(24, TipoVazamento.REDE.getMetaHoras());
        assertEquals(72, TipoVazamento.RAMAL.getMetaHoras());
        assertEquals(168, TipoVazamento.CLANDESTINA.getMetaHoras());
        assertEquals(48, TipoVazamento.DESPERDICIO.getMetaHoras());
    }
}
