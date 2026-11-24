package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.ConquistaDTO;

import java.util.List;

/** Regras das conquistas. Elas são calculadas a partir dos registros existentes, nada é gravado. */
public final class Conquistas {

    /** Consumo médio de uma família em um mês, em litros (mesmo valor usado no protótipo). */
    public static final long LITROS_FAMILIA_MES = 12000;

    private Conquistas() {
    }

    public static List<ConquistaDTO> calcular(int registrados, int resolvidos, int apoiosDados,
                                              int avaliacoesFeitas, long litrosMapeados) {
        return List.of(
                new ConquistaDTO("PRIMEIRO_AVISO", "Primeiro aviso",
                        "Registrou o primeiro vazamento.", registrados >= 1),
                new ConquistaDTO("OLHO_NA_RUA", "Olho na rua",
                        "Registrou 5 vazamentos.", registrados >= 5),
                new ConquistaDTO("GUARDIAO_DA_AGUA", "Guardião da água",
                        "Registrou 10 vazamentos.", registrados >= 10),
                new ConquistaDTO("EU_TAMBEM_VI", "Eu também vi",
                        "Reforçou o aviso de outra pessoa.", apoiosDados >= 1),
                new ConquistaDTO("VIZINHANCA_ATENTA", "Vizinhança atenta",
                        "Reforçou 5 avisos de outras pessoas.", apoiosDados >= 5),
                new ConquistaDTO("PROBLEMA_RESOLVIDO", "Problema resolvido",
                        "Um dos seus avisos foi resolvido.", resolvidos >= 1),
                new ConquistaDTO("OPINIAO_QUE_CONTA", "Opinião que conta",
                        "Avaliou um atendimento.", avaliacoesFeitas >= 1),
                new ConquistaDTO("FAMILIA_INTEIRA", "Uma família inteira",
                        "Seus avisos mapearam o consumo de uma família por mês (12.000 litros).",
                        litrosMapeados >= LITROS_FAMILIA_MES));
    }
}
