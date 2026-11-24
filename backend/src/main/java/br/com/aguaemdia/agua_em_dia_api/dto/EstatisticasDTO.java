package br.com.aguaemdia.agua_em_dia_api.dto;

import java.util.List;

public record EstatisticasDTO(
        int registrados,
        int emAberto,
        int resolvidos,
        long litrosMapeados,
        int apoiosDados,
        int avaliacoesFeitas,
        List<ConquistaDTO> conquistas) {
}
