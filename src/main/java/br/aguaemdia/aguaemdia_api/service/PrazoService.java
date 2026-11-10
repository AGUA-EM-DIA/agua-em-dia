package br.com.aguaemdia.service;

import br.com.aguaemdia.domain.HistoricoPrazo;
import br.com.aguaemdia.domain.MatrizPrazoAtendimento;
import br.com.aguaemdia.domain.Vazamento;
import br.com.aguaemdia.repository.HistoricoPrazoRepository;
import br.com.aguaemdia.repository.MatrizPrazoAtendimentoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Prazo-meta efetivo (RN-AD08): porte × infraestrutura, congelado na ocorrência para alterações futuras da matriz não a afetarem. */
@Service
public class PrazoService {

    public record PrazoEfetivo(int horas, Long matrizId) {}

    private final MatrizPrazoAtendimentoRepository matriz;
    private final HistoricoPrazoRepository historico;

    public PrazoService(MatrizPrazoAtendimentoRepository matriz, HistoricoPrazoRepository historico) {
        this.matriz = matriz;
        this.historico = historico;
    }

    /** Sem infraestrutura informada vale o MENOR prazo da linha do porte, sem classificar como adutora. */
    @Transactional(readOnly = true)
    public Optional<PrazoEfetivo> resolver(Long porteId, Long infraestruturaId) {
        if (infraestruturaId != null) {
            return matriz.findByPorteIdAndInfraestruturaIdAndVigenteAteIsNull(porteId, infraestruturaId)
                    .map(m -> new PrazoEfetivo(m.getPrazoHoras(), m.getId()));
        }
        List<MatrizPrazoAtendimento> linha = matriz.findByPorteIdAndVigenteAteIsNull(porteId);
        return linha.stream().min(Comparator.comparingInt(MatrizPrazoAtendimento::getPrazoHoras))
                .map(m -> new PrazoEfetivo(m.getPrazoHoras(), m.getId()));
    }

    /** Congela (ou recalcula, na reclassificação) o prazo da ocorrência e registra o histórico. */
    @Transactional
    public void definir(Vazamento v, Long porteId, Long infraestruturaId, String motivo) {
        Optional<PrazoEfetivo> novo = resolver(porteId, infraestruturaId);
        Integer anterior = v.getPrazoEfetivoHoras();
        Integer horas = novo.map(PrazoEfetivo::horas).orElse(null);
        v.setPrazoEfetivoHoras(horas);
        v.setMatrizPrazoId(novo.map(PrazoEfetivo::matrizId).orElse(null));
        if (!Objects.equals(anterior, horas)) {
            HistoricoPrazo h = new HistoricoPrazo();
            h.setVazamentoId(v.getId());
            h.setPrazoAnterior(anterior);
            h.setPrazoNovo(horas);
            h.setMotivo(motivo);
            historico.save(h);
        }
    }
}
