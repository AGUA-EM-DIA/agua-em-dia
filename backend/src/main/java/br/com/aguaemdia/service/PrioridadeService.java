package br.com.aguaemdia.service;

import br.com.aguaemdia.calculo.CalculoPerdas;
import br.com.aguaemdia.calculo.FormulaPrioridade;
import br.com.aguaemdia.domain.CategoriaOcorrencia;
import br.com.aguaemdia.domain.HistoricoPrioridade;
import br.com.aguaemdia.domain.IntervaloEstimativa;
import br.com.aguaemdia.domain.Vazamento;
import br.com.aguaemdia.domain.VazamentoStatus;
import br.com.aguaemdia.repository.CategoriaOcorrenciaRepository;
import br.com.aguaemdia.repository.HistoricoPrioridadeRepository;
import br.com.aguaemdia.repository.IntervaloEstimativaRepository;
import br.com.aguaemdia.repository.RelatoRepository;
import br.com.aguaemdia.repository.VazamentoRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Prioridade P = Q×(1+A)×(1+R)×(1+G) (RF015, RN-AD09) com histórico dos parâmetros usados. */
@Service
public class PrioridadeService {

    private static final BigDecimal LIMIAR = new BigDecimal("0.000001");

    private final VazamentoRepository vazamentos;
    private final RelatoRepository relatos;
    private final CategoriaOcorrenciaRepository categorias;
    private final IntervaloEstimativaRepository intervalos;
    private final HistoricoPrioridadeRepository historico;
    private final ParametroService parametros;
    private final ObjectMapper mapper;
    private final Clock clock;

    public PrioridadeService(VazamentoRepository vazamentos, RelatoRepository relatos,
            CategoriaOcorrenciaRepository categorias, IntervaloEstimativaRepository intervalos,
            HistoricoPrioridadeRepository historico, ParametroService parametros, ObjectMapper mapper, Clock clock) {
        this.vazamentos = vazamentos;
        this.relatos = relatos;
        this.categorias = categorias;
        this.intervalos = intervalos;
        this.historico = historico;
        this.parametros = parametros;
        this.mapper = mapper;
        this.clock = clock;
    }

    private FormulaPrioridade.Parametros parametrosFormula() {
        return new FormulaPrioridade.Parametros(
                parametros.decimal("prioridade.bonus_por_cidadao"), parametros.decimal("prioridade.bonus_maximo_relatos"),
                parametros.decimal("prioridade.bonus_por_vizinho"), parametros.decimal("prioridade.bonus_maximo_concentracao"));
    }

    /** Recalcula e grava a prioridade. Só registra histórico se o valor mudou. */
    @Transactional
    public void recalcular(Long vazamentoId, String motivo) {
        Optional<Vazamento> opt = vazamentos.findById(vazamentoId);
        if (opt.isEmpty()) return;
        Vazamento v = opt.get();
        OffsetDateTime agora = OffsetDateTime.now(clock);
        VazamentoStatus status = VazamentoStatus.valueOf(v.getStatus());

        Map<String, Object> usados = new LinkedHashMap<>();
        usados.put("motivo", motivo);
        BigDecimal pontuacao = null;

        boolean fisica = categorias.findById(v.getCategoriaId()).map(CategoriaOcorrencia::getExigeEstimativaFisica).orElse(false);
        if (VazamentoStatus.ATIVOS.contains(status) && fisica && !Boolean.TRUE.equals(v.getVazaoIncerta())) {
            Optional<IntervaloEstimativa> aberto = intervalos.findByVazamentoIdAndFimIsNullAndSituacao(vazamentoId, "ATIVO");
            BigDecimal q = aberto.map(IntervaloEstimativa::getVazaoLH).orElse(null);
            BigDecimal horas = CalculoPerdas.horas(v.getPrazoInicioEm(), agora);
            FormulaPrioridade.Parametros p = parametrosFormula();
            int cidadaos = (int) relatos.contarCidadaosDistintos(vazamentoId);
            int vizinhos = vazamentos.contarVizinhosAtivos(vazamentoId, parametros.duplo("concentracao.raio_metros"),
                    VazamentoStatus.nomes(VazamentoStatus.ABERTOS_CONFIRMADOS));
            BigDecimal a = FormulaPrioridade.fatorAtraso(horas, v.getPrazoEfetivoHoras());
            BigDecimal r = FormulaPrioridade.fatorRelatos(cidadaos, p);
            BigDecimal g = FormulaPrioridade.fatorConcentracao(vizinhos, p);
            pontuacao = FormulaPrioridade.pontuacao(q, a, r, g);
            usados.put("Q_vazao_l_h", q);
            usados.put("T_horas", horas);
            usados.put("M_prazo_horas", v.getPrazoEfetivoHoras());
            usados.put("A", a);
            usados.put("N_cidadaos", cidadaos);
            usados.put("R", r);
            usados.put("C_vizinhos", vizinhos);
            usados.put("G", g);
        } else {
            usados.put("sem_pontuacao", "categoria sem estimativa física, vazão não confiável ou ocorrência encerrada");
        }

        boolean mudou = mudou(v.getPrioridadeAtual(), pontuacao);
        vazamentos.atualizarPrioridade(vazamentoId, pontuacao, agora);
        if (mudou) {
            HistoricoPrioridade h = new HistoricoPrioridade();
            h.setVazamentoId(vazamentoId);
            h.setPontuacao(pontuacao);
            try {
                h.setParametrosUtilizados(mapper.writeValueAsString(usados));
            } catch (JsonProcessingException e) {
                h.setParametrosUtilizados("{}");
            }
            historico.save(h);
        }
    }

    private static boolean mudou(BigDecimal antes, BigDecimal depois) {
        if (antes == null && depois == null) return false;
        if (antes == null || depois == null) return true;
        return antes.subtract(depois).abs().compareTo(LIMIAR) > 0;
    }

    @Transactional(readOnly = true)
    public List<Long> idsParaRecalculoPeriodico() {
        return vazamentos.findByStatusIn(VazamentoStatus.nomes(VazamentoStatus.ATIVOS)).stream().map(Vazamento::getId).toList();
    }
}
