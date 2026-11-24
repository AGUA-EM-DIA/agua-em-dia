package br.com.aguaemdia.agua_em_dia_api.service;

import br.com.aguaemdia.agua_em_dia_api.dto.AvaliacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.AvaliacaoRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.VazamentoCadastroDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.VazamentoRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.Apoio;
import br.com.aguaemdia.agua_em_dia_api.entity.Avaliacao;
import br.com.aguaemdia.agua_em_dia_api.entity.SituacaoVazamento;
import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.entity.Vazamento;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import br.com.aguaemdia.agua_em_dia_api.repository.ApoioRepository;
import br.com.aguaemdia.agua_em_dia_api.repository.AvaliacaoRepository;
import br.com.aguaemdia.agua_em_dia_api.repository.UsuarioRepository;
import br.com.aguaemdia.agua_em_dia_api.repository.VazamentoRepository;
import br.com.aguaemdia.agua_em_dia_api.util.Tempo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Regras dos vazamentos: registrar, consultar, editar a descrição, reforçar, avaliar e atualizar a situação. */
@Service
public class VazamentoService {

    private final VazamentoRepository vazamentos;
    private final ApoioRepository apoios;
    private final AvaliacaoRepository avaliacoes;
    private final UsuarioRepository usuarios;
    private final ArmazenamentoImagemService imagens;
    private final boolean demo;

    public VazamentoService(VazamentoRepository vazamentos, ApoioRepository apoios, AvaliacaoRepository avaliacoes,
                            UsuarioRepository usuarios, ArmazenamentoImagemService imagens,
                            @Value("${app.demo:false}") boolean demo) {
        this.vazamentos = vazamentos;
        this.apoios = apoios;
        this.avaliacoes = avaliacoes;
        this.usuarios = usuarios;
        this.imagens = imagens;
        this.demo = demo;
    }

    /** Registra um vazamento. O dono é sempre o usuário autenticado. A foto é opcional. */
    @Transactional
    public VazamentoRespostaDTO registrar(Long usuarioId, VazamentoCadastroDTO dados, MultipartFile foto) {
        Usuario autor = usuarios.findById(usuarioId)
                .orElseThrow(() -> NegocioException.naoAutenticado("Sessão inválida. Entre novamente."));

        // Valida a foto antes de gravar qualquer coisa
        ArmazenamentoImagemService.Imagem imagem = (foto == null || foto.isEmpty()) ? null : imagens.validar(foto);
        String fotoUrl = imagem == null ? null : imagens.salvar(imagem);

        Vazamento v = new Vazamento();
        v.setUsuario(autor);
        v.setTipo(dados.tipo());
        v.setPorte(dados.porte());
        v.setSituacao(SituacaoVazamento.ABERTO);
        v.setDescricao(textoOuNulo(dados.descricao()));
        v.setEndereco(textoOuNulo(dados.endereco()));
        v.setLatitude(dados.latitude());
        v.setLongitude(dados.longitude());
        v.setFotoUrl(fotoUrl);

        try {
            vazamentos.saveAndFlush(v);
        } catch (RuntimeException e) {
            imagens.remover(fotoUrl); // não deixa foto solta se o registro falhou
            throw e;
        }
        return montar(List.of(v), usuarioId).get(0);
    }

    /** Todos os vazamentos (o mapa e a fila mostram os de todos os cidadãos). */
    @Transactional(readOnly = true)
    public List<VazamentoRespostaDTO> listarTodos(Long usuarioId) {
        return montar(vazamentos.listarTodos(), usuarioId);
    }

    /** Somente os vazamentos do cidadão autenticado. */
    @Transactional(readOnly = true)
    public List<VazamentoRespostaDTO> listarDoUsuario(Long usuarioId) {
        return montar(vazamentos.listarDoUsuario(usuarioId), usuarioId);
    }

    @Transactional(readOnly = true)
    public VazamentoRespostaDTO buscar(Long usuarioId, Long id) {
        return montar(List.of(buscarOuFalhar(id)), usuarioId).get(0);
    }

    /** Edita somente a descrição, e só nos vazamentos do próprio cidadão. */
    @Transactional
    public VazamentoRespostaDTO editarDescricao(Long usuarioId, Long id, String descricao) {
        Vazamento v = buscarOuFalhar(id);
        exigirDono(v, usuarioId, "Você só pode editar os seus próprios registros.");
        v.setDescricao(textoOuNulo(descricao));
        vazamentos.saveAndFlush(v);
        return montar(List.of(v), usuarioId).get(0);
    }

    /** "Eu também vi": reforça o vazamento de outra pessoa, uma vez só, enquanto ele não foi resolvido. */
    @Transactional
    public VazamentoRespostaDTO apoiar(Long usuarioId, Long id) {
        Vazamento v = buscarOuFalhar(id);
        if (v.getUsuario().getId().equals(usuarioId)) {
            throw NegocioException.naoProcessavel("Você não pode reforçar o seu próprio registro.");
        }
        if (v.getSituacao() == SituacaoVazamento.RESOLVIDO) {
            throw NegocioException.conflito("Esse vazamento já foi resolvido.");
        }
        if (apoios.existsByUsuarioIdAndVazamentoId(usuarioId, id)) {
            throw NegocioException.conflito("Você já reforçou esse vazamento.");
        }
        Usuario usuario = usuarios.getReferenceById(usuarioId);
        apoios.saveAndFlush(new Apoio(usuario, v));
        return montar(List.of(v), usuarioId).get(0);
    }

    /** Avaliação do atendimento: só o dono, só depois de resolvido, uma vez. */
    @Transactional
    public VazamentoRespostaDTO avaliar(Long usuarioId, Long id, AvaliacaoDTO dados) {
        Vazamento v = buscarOuFalhar(id);
        exigirDono(v, usuarioId, "Você só pode avaliar os seus próprios registros.");
        if (v.getSituacao() != SituacaoVazamento.RESOLVIDO) {
            throw NegocioException.conflito("A avaliação abre quando o reparo for concluído.");
        }
        if (avaliacoes.existsByVazamentoId(id)) {
            throw NegocioException.conflito("Esse atendimento já foi avaliado.");
        }
        avaliacoes.saveAndFlush(new Avaliacao(v, dados.nota(), textoOuNulo(dados.comentario())));
        return montar(List.of(v), usuarioId).get(0);
    }

    /**
     * Atualiza a situação (ABERTO, REPARO, RESOLVIDO). Só avança, nunca volta.
     * Quem pode chamar isso é decidido no controller (chave de serviço ou modo demonstração).
     */
    @Transactional
    public VazamentoRespostaDTO atualizarSituacao(Long id, SituacaoVazamento nova, Long usuarioId) {
        Vazamento v = buscarOuFalhar(id);
        if (nova.ordinal() <= v.getSituacao().ordinal()) {
            throw NegocioException.conflito("A situação só pode avançar: de " + v.getSituacao() + " para " + nova + " não é possível.");
        }
        LocalDateTime agora = Tempo.agora();
        if (nova == SituacaoVazamento.REPARO) {
            v.setDespachadoEm(agora);
        } else if (nova == SituacaoVazamento.RESOLVIDO) {
            v.setConcluidoEm(agora);
        }
        v.setSituacao(nova);
        v.setNovidadeNaoVista(true);
        vazamentos.saveAndFlush(v);
        return montar(List.of(v), usuarioId).get(0);
    }

    /** O cidadão abriu o perfil: as novidades dos registros dele deixam de aparecer como "não vistas". */
    @Transactional
    public void marcarNovidadesComoVistas(Long usuarioId) {
        vazamentos.marcarNovidadesComoVistas(usuarioId);
    }

    // ---------- apoio interno ----------

    private Vazamento buscarOuFalhar(Long id) {
        return vazamentos.buscarPorId(id)
                .orElseThrow(() -> NegocioException.naoEncontrado("Vazamento não encontrado."));
    }

    private void exigirDono(Vazamento v, Long usuarioId, String mensagem) {
        if (!v.getUsuario().getId().equals(usuarioId)) {
            throw NegocioException.proibido(mensagem);
        }
    }

    private static String textoOuNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    /** Converte para a resposta da API, buscando de uma vez os reforços e avaliações (sem consultar um por um). */
    private List<VazamentoRespostaDTO> montar(List<Vazamento> lista, Long usuarioId) {
        Map<Long, Long> totaisDeApoio = new HashMap<>();
        for (ApoioRepository.TotalPorVazamento t : apoios.totaisPorVazamento()) {
            totaisDeApoio.put(t.getVazamentoId(), t.getTotal());
        }
        Set<Long> meusApoios = apoios.idsDosVazamentosApoiadosPor(usuarioId);
        Map<Long, Avaliacao> avaliacaoPorVazamento = new HashMap<>();
        for (Avaliacao a : avaliacoes.listarTodas()) {
            avaliacaoPorVazamento.put(a.getVazamento().getId(), a);
        }

        LocalDateTime agora = Tempo.agora();
        return lista.stream()
                .map(v -> paraResposta(v, usuarioId, agora,
                        totaisDeApoio.getOrDefault(v.getId(), 0L).intValue(),
                        meusApoios.contains(v.getId()),
                        avaliacaoPorVazamento.get(v.getId())))
                .toList();
    }

    private VazamentoRespostaDTO paraResposta(Vazamento v, Long usuarioId, LocalDateTime agora,
                                              int totalApoios, boolean apoiadoPorMim, Avaliacao avaliacao) {
        boolean meu = v.getUsuario().getId().equals(usuarioId);
        Double horasAteReparo = null;
        if (v.getSituacao() == SituacaoVazamento.RESOLVIDO && v.getConcluidoEm() != null) {
            horasAteReparo = Duration.between(v.getCriadoEm(), v.getConcluidoEm()).toMillis() / 3_600_000.0;
        }
        // O comentário da avaliação é do dono; no modo demonstração o painel de operação também o mostra
        AvaliacaoRespostaDTO avaliacaoDto = avaliacao == null ? null : new AvaliacaoRespostaDTO(
                avaliacao.getNota(),
                (meu || demo) ? avaliacao.getComentario() : null,
                Tempo.paraMillis(avaliacao.getCriadoEm()));
        return new VazamentoRespostaDTO(
                v.getId(),
                v.protocolo(),
                v.getTipo(),
                v.getPorte(),
                v.getDescricao(),
                v.getEndereco(),
                v.getLatitude(),
                v.getLongitude(),
                v.getFotoUrl(),
                v.getSituacao(),
                v.vazaoLitrosHora(),
                v.getTipo().getMetaHoras(),
                Tempo.paraMillis(v.getCriadoEm()),
                Tempo.paraMillis(v.getDespachadoEm()),
                Tempo.paraMillis(v.getConcluidoEm()),
                horasAteReparo,
                v.litrosPerdidos(agora),
                primeiroNome(v.getUsuario().getNome()),
                meu,
                totalApoios,
                apoiadoPorMim,
                meu && v.isNovidadeNaoVista(),
                avaliacaoDto);
    }

    /** Mostra aos outros cidadãos só o primeiro nome do autor. */
    private static String primeiroNome(String nome) {
        String limpo = nome == null ? "" : nome.trim();
        int espaco = limpo.indexOf(' ');
        return espaco > 0 ? limpo.substring(0, espaco) : limpo;
    }
}
