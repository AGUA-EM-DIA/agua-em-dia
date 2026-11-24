package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.EstatisticasDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.SituacaoVazamento;
import br.com.aguaemdia.agua_em_dia_api.entity.Vazamento;
import br.com.aguaemdia.agua_em_dia_api.repository.ApoioRepository;
import br.com.aguaemdia.agua_em_dia_api.repository.AvaliacaoRepository;
import br.com.aguaemdia.agua_em_dia_api.repository.VazamentoRepository;
import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Estatísticas pessoais e conquistas. Tudo é calculado a partir dos registros existentes. */
@Service
public class EstatisticaService {

    private final VazamentoRepository vazamentos;
    private final ApoioRepository apoios;
    private final AvaliacaoRepository avaliacoes;

    public EstatisticaService(VazamentoRepository vazamentos, ApoioRepository apoios, AvaliacaoRepository avaliacoes) {
        this.vazamentos = vazamentos;
        this.apoios = apoios;
        this.avaliacoes = avaliacoes;
    }

    @Transactional(readOnly = true)
    public EstatisticasDTO doUsuario(Long usuarioId) {
        List<Vazamento> meus = vazamentos.listarDoUsuario(usuarioId);
        LocalDateTime agora = Tempo.agora();

        int registrados = meus.size();
        int resolvidos = (int) meus.stream().filter(v -> v.getSituacao() == SituacaoVazamento.RESOLVIDO).count();
        long litros = meus.stream().mapToLong(v -> v.litrosPerdidos(agora)).sum();
        int apoiosDados = (int) apoios.countByUsuarioId(usuarioId);
        int avaliacoesFeitas = (int) avaliacoes.countByVazamentoUsuarioId(usuarioId);

        return new EstatisticasDTO(registrados, registrados - resolvidos, resolvidos, litros, apoiosDados,
                avaliacoesFeitas, Conquistas.calcular(registrados, resolvidos, apoiosDados, avaliacoesFeitas, litros));
    }
}
