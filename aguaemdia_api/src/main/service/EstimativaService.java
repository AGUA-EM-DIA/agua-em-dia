package br.com.aguaemdia.service;

import br.com.aguaemdia.calculo.CalculoPerdas;
import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.domain.ClassificacaoTecnica;
import br.com.aguaemdia.domain.IntervaloEstimativa;
import br.com.aguaemdia.domain.PorteVazamento;
import br.com.aguaemdia.domain.RevisaoCalculo;
import br.com.aguaemdia.repository.ClassificacaoTecnicaRepository;
import br.com.aguaemdia.repository.IntervaloEstimativaRepository;
import br.com.aguaemdia.repository.PorteVazamentoRepository;
import br.com.aguaemdia.repository.RelatoRepository;
import br.com.aguaemdia.repository.RevisaoCalculoRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Intervalos de perda (RF029): volume = Σ vazão × duração de intervalos ATIVOS e NÃO sobrepostos.
 * Os métodos de escrita assumem que quem chama já travou a ocorrência (VazamentoRepository.findByIdParaAtualizar),
 * é isso que garante a não sobreposição — o SQL Server não tem "exclusion constraint".
 */
@Service
public class EstimativaService {

    private final IntervaloEstimativaRepository intervalos;
    private final RevisaoCalculoRepository revisoes;
    private final RelatoRepository relatos;
    private final ClassificacaoTecnicaRepository classificacoes;
    private final PorteVazamentoRepository portes;
    private final Clock clock;

    public EstimativaService(IntervaloEstimativaRepository intervalos, RevisaoCalculoRepository revisoes,
            RelatoRepository relatos, ClassificacaoTecnicaRepository classificacoes,
            PorteVazamentoRepository portes, Clock clock) {
        this.intervalos = intervalos;
        this.revisoes = revisoes;
        this.relatos = relatos;
        this.classificacoes = classificacoes;
        this.portes = portes;
        this.clock = clock;
    }

    public static CalculoPerdas.Intervalo paraCalculo(IntervaloEstimativa i) {
        return new CalculoPerdas.Intervalo(i.getInicio(), i.getFim(), i.getVazaoLH());
    }

    /** Classificação técnica vigente prevalece; senão vale o MAIOR porte informado pelos cidadãos. */
    @Transactional(readOnly = true)
    public BigDecimal vazaoDe(Long vazamentoId) {
        Optional<ClassificacaoTecnica> c = classificacoes.findByVazamentoIdAndVigenteAteIsNull(vazamentoId);
        if (c.isPresent()) {
            return portes.findById(c.get().getPorteId()).map(PorteVazamento::getVazaoBaseLH).orElse(null);
        }
        List<PorteVazamento> informados = relatos.portesInformados(vazamentoId, PageRequest.of(0, 1));
        return informados.isEmpty() ? null : informados.get(0).getVazaoBaseLH();
    }

    @Transactional(readOnly = true)
    public Optional<IntervaloEstimativa> emCurso(Long vazamentoId) {
        return intervalos.findByVazamentoIdAndFimIsNullAndSituacao(vazamentoId, "ATIVO");
    }

    /** Fim do último intervalo ativo já encerrado (usado para não sobrepor ao reabrir). */
    @Transactional(readOnly = true)
    public Optional<OffsetDateTime> ultimoFim(Long vazamentoId) {
        return intervalos.findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO").stream()
                .map(IntervaloEstimativa::getFim).filter(Objects::nonNull).max(OffsetDateTime::compareTo);
    }

    @Transactional
    public IntervaloEstimativa iniciar(Long vazamentoId, OffsetDateTime inicio, BigDecimal vazao) {
        Optional<IntervaloEstimativa> aberto = emCurso(vazamentoId);
        if (aberto.isPresent()) return aberto.get();
        for (IntervaloEstimativa i : intervalos.findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO")) {
            if (i.getFim() != null && i.getFim().isAfter(inicio)) {
                throw ApiException.negocio("O novo intervalo de perda sobreporia um intervalo já registrado.");
            }
        }
        IntervaloEstimativa n = new IntervaloEstimativa();
        n.setVazamentoId(vazamentoId);
        n.setInicio(inicio);
        n.setVazaoLH(vazao);
        n.setSituacao("ATIVO");
        n.setVersaoCalculo(versaoAtual(vazamentoId));
        return intervalos.save(n);
    }

    /** Encerra a perda no horário efetivo do reparo. */
    @Transactional
    public void fechar(Long vazamentoId, OffsetDateTime fim) {
        IntervaloEstimativa i = emCurso(vazamentoId).orElse(null);
        if (i == null) return;                                   // categoria sem estimativa física
        if (!fim.isAfter(i.getInicio())) {
            throw ApiException.negocio("O horário efetivo do reparo deve ser posterior ao início da perda estimada.");
        }
        i.setFim(fim);
    }

    @Transactional
    public void cancelarTodos(Long vazamentoId, String motivo, Long ator) {
        List<IntervaloEstimativa> ativos = intervalos.findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO");
        if (ativos.isEmpty()) return;
        int anterior = versaoAtual(vazamentoId);
        for (IntervaloEstimativa i : ativos) i.setSituacao("CANCELADO");
        registrarRevisao(vazamentoId, motivo, anterior, anterior + 1, ator);
    }

    /** Correção retroativa da vazão (classificação técnica ou triagem). vazao == null marca como não confiável. */
    @Transactional
    public void definirVazao(Long vazamentoId, BigDecimal vazao, String motivo, Long ator) {
        List<IntervaloEstimativa> ativos = intervalos.findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO");
        if (ativos.isEmpty()) return;
        int anterior = versaoAtual(vazamentoId);
        for (IntervaloEstimativa i : ativos) {
            i.setVazaoLH(vazao);
            i.setVersaoCalculo(anterior + 1);
        }
        registrarRevisao(vazamentoId, motivo, anterior, anterior + 1, ator);
    }

    /** RN-AD05: o marco de início acompanha o relato válido mais antigo (usado ao consolidar/reverter duplicidades). */
    @Transactional
    public void ajustarInicioPrimeiro(Long vazamentoId, OffsetDateTime novoInicio, String motivo, Long ator) {
        List<IntervaloEstimativa> ativos = intervalos.findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO");
        if (ativos.isEmpty()) return;
        IntervaloEstimativa primeiro = ativos.get(0);
        if (primeiro.getInicio().isEqual(novoInicio)) return;
        if (primeiro.getFim() != null && !novoInicio.isBefore(primeiro.getFim())) {
            throw ApiException.negocio("Não é possível ajustar o marco inicial: o novo horário ultrapassa o fim do primeiro intervalo.");
        }
        int anterior = versaoAtual(vazamentoId);
        primeiro.setInicio(novoInicio);
        for (IntervaloEstimativa i : ativos) i.setVersaoCalculo(anterior + 1);
        registrarRevisao(vazamentoId, motivo, anterior, anterior + 1, ator);
    }

    @Transactional(readOnly = true)
    public CalculoPerdas.Resultado volume(Long vazamentoId, OffsetDateTime de, OffsetDateTime ate) {
        List<CalculoPerdas.Intervalo> lista = intervalos
                .findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO").stream()
                .map(EstimativaService::paraCalculo).toList();
        return CalculoPerdas.volume(lista, de, ate, OffsetDateTime.now(clock));
    }

    /** Volume acumulado desde o primeiro relato válido até agora. */
    @Transactional(readOnly = true)
    public CalculoPerdas.Resultado volumeTotal(Long vazamentoId) {
        OffsetDateTime agora = OffsetDateTime.now(clock);
        return volume(vazamentoId, agora.minusYears(50), agora);
    }

    private int versaoAtual(Long vazamentoId) {
        return intervalos.findByVazamentoIdAndSituacaoOrderByInicioAsc(vazamentoId, "ATIVO").stream()
                .mapToInt(IntervaloEstimativa::getVersaoCalculo).max().orElse(1);
    }

    private void registrarRevisao(Long vazamentoId, String motivo, int anterior, int nova, Long ator) {
        RevisaoCalculo r = new RevisaoCalculo();
        r.setVazamentoId(vazamentoId);
        r.setMotivo(motivo == null ? "Revisão de cálculo" : motivo);
        r.setVersaoAnterior(anterior);
        r.setVersaoNova(nova);
        r.setExecutadoPor(ator);
        revisoes.save(r);
    }
}
