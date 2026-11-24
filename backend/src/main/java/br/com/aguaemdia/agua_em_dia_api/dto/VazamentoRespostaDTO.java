package br.com.aguaemdia.agua_em_dia_api.dto;

import br.com.aguaemdia.agua_em_dia_api.entity.PorteVazamento;
import br.com.aguaemdia.agua_em_dia_api.entity.SituacaoVazamento;
import br.com.aguaemdia.agua_em_dia_api.entity.TipoVazamento;

import java.math.BigDecimal;

/**
 * Vazamento como o frontend precisa ver. Datas em milissegundos (UTC).
 * "autor" traz só o primeiro nome; "meu" diz se o vazamento é do cidadão autenticado.
 */
public record VazamentoRespostaDTO(
        Long id,
        String protocolo,
        TipoVazamento tipo,
        PorteVazamento porte,
        String descricao,
        String endereco,
        BigDecimal latitude,
        BigDecimal longitude,
        String fotoUrl,
        SituacaoVazamento situacao,
        int vazaoLitrosHora,
        int metaHoras,
        long criadoEm,
        Long despachadoEm,
        Long concluidoEm,
        Double horasAteReparo,
        long litrosPerdidos,
        String autor,
        boolean meu,
        int apoios,
        boolean apoiadoPorMim,
        boolean novidadeNaoVista,
        AvaliacaoRespostaDTO avaliacao) {
}
