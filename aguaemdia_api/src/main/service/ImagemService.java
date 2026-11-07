package br.com.aguaemdia.service;

import br.com.aguaemdia.common.AposCommit;
import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.common.UsuarioLogado;
import br.com.aguaemdia.domain.Atendimento;
import br.com.aguaemdia.domain.AtribuicaoEquipe;
import br.com.aguaemdia.domain.Imagem;
import br.com.aguaemdia.domain.Relato;
import br.com.aguaemdia.repository.AtendimentoRepository;
import br.com.aguaemdia.repository.AtribuicaoEquipeRepository;
import br.com.aguaemdia.repository.ImagemRepository;
import br.com.aguaemdia.repository.MembroEquipeRepository;
import br.com.aguaemdia.repository.RelatoRepository;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** RF008/RF033: até 3 imagens por relato e por atendimento; formato verificado pelo CONTEÚDO, não pelo nome. */
@Service
public class ImagemService {

    public record Conteudo(String tipoMime, byte[] bytes, String nome) {}

    private record Validada(String mime, String extensao, byte[] bytes, String nomeOriginal) {}

    private static final Set<String> MARCAS_HEIC = Set.of("heic", "heix", "hevc", "mif1", "msf1", "heim", "heis");

    private final ImagemRepository imagens;
    private final RelatoRepository relatos;
    private final AtendimentoRepository atendimentos;
    private final AtribuicaoEquipeRepository atribuicoes;
    private final MembroEquipeRepository membros;
    private final ArmazenamentoService armazenamento;
    private final ParametroService parametros;

    public ImagemService(ImagemRepository imagens, RelatoRepository relatos, AtendimentoRepository atendimentos,
            AtribuicaoEquipeRepository atribuicoes, MembroEquipeRepository membros,
            ArmazenamentoService armazenamento, ParametroService parametros) {
        this.imagens = imagens;
        this.relatos = relatos;
        this.atendimentos = atendimentos;
        this.atribuicoes = atribuicoes;
        this.membros = membros;
        this.armazenamento = armazenamento;
        this.parametros = parametros;
    }

    @Transactional
    public List<Imagem> anexarAoRelato(Long relatoId, List<MultipartFile> arquivos, Long usuarioId) {
        List<Validada> validas = validar(arquivos);
        if (validas.isEmpty()) return List.of();
        exigirLimite(imagens.countByRelatoId(relatoId), validas.size());
        return gravar(validas, "relatos/" + relatoId, relatoId, null, usuarioId);
    }

    /** O chamador deve ter travado o atendimento (findByIdParaAtualizar) para o limite valer sob concorrência. */
    @Transactional
    public List<Imagem> anexarAoAtendimento(Long atendimentoId, List<MultipartFile> arquivos, Long usuarioId) {
        List<Validada> validas = validar(arquivos);
        if (validas.isEmpty()) return List.of();
        exigirLimite(imagens.countByAtendimentoId(atendimentoId), validas.size());
        return gravar(validas, "atendimentos/" + atendimentoId, null, atendimentoId, usuarioId);
    }

    private void exigirLimite(long existentes, int novas) {
        int max = parametros.inteiro("imagem.maximo_por_contexto");
        if (existentes + novas > max) {
            throw ApiException.negocio("Limite de " + max + " imagens excedido.");
        }
    }

    private List<Imagem> gravar(List<Validada> validas, String pasta, Long relatoId, Long atendimentoId, Long usuarioId) {
        List<Imagem> salvas = new ArrayList<>();
        for (Validada v : validas) {
            String caminho = armazenamento.salvar(pasta, v.bytes(), v.extensao());
            AposCommit.aoDesfazer(() -> armazenamento.apagar(caminho));   // transação desfeita => sem arquivo órfão
            Imagem i = new Imagem();
            i.setRelatoId(relatoId);
            i.setAtendimentoId(atendimentoId);
            i.setCaminhoArquivo(caminho);
            i.setNomeOriginal(Textos.truncar(v.nomeOriginal(), 255));
            i.setTipoMime(v.mime());
            i.setTamanhoBytes((long) v.bytes().length);
            i.setCriadoPor(usuarioId);
            salvas.add(imagens.save(i));
        }
        return salvas;
    }

    private List<Validada> validar(List<MultipartFile> arquivos) {
        List<Validada> out = new ArrayList<>();
        if (arquivos == null) return out;
        long limite = parametros.longo("imagem.limite_bytes");
        for (MultipartFile f : arquivos) {
            if (f == null || f.isEmpty()) continue;
            if (f.getSize() > limite) throw ApiException.negocio("Imagem acima do tamanho máximo permitido.");
            byte[] bytes;
            try {
                bytes = f.getBytes();
            } catch (IOException e) {
                throw ApiException.invalido("Não foi possível ler a imagem enviada.");
            }
            String[] tipo = detectar(bytes);
            if (tipo == null) throw ApiException.negocio("Formato de imagem não suportado (use JPEG, PNG, WebP ou HEIC).");
            out.add(new Validada(tipo[0], tipo[1], bytes, f.getOriginalFilename()));
        }
        return out;
    }

    /** Detecção por assinatura ("magic bytes"). Devolve {mime, extensão} ou null. */
    static String[] detectar(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) return new String[] {"image/jpeg", "jpg"};
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') return new String[] {"image/png", "png"};
        if (b.length >= 12 && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P') return new String[] {"image/webp", "webp"};
        if (b.length >= 12 && b[4] == 'f' && b[5] == 't' && b[6] == 'y' && b[7] == 'p') {
            String marca = new String(b, 8, 4, StandardCharsets.US_ASCII);
            if (MARCAS_HEIC.contains(marca)) return new String[] {"image/heic", "heic"};
        }
        return null;
    }

    /** Entrega o arquivo se o usuário puder vê-lo: autor, operação, autor do vazamento ou equipe do atendimento. */
    @Transactional(readOnly = true)
    public Conteudo obter(Long imagemId, UsuarioLogado u) {
        Imagem i = imagens.findById(imagemId).orElseThrow(() -> ApiException.naoEncontrado("Imagem não encontrada."));
        boolean permitido = u.ehOperacao();
        if (!permitido && i.getRelatoId() != null) {
            Relato r = relatos.findById(i.getRelatoId()).orElse(null);
            permitido = r != null && r.getUsuarioId().equals(u.id());
        }
        if (!permitido && i.getAtendimentoId() != null) {
            Atendimento a = atendimentos.findById(i.getAtendimentoId()).orElse(null);
            if (a != null) {
                permitido = relatos.existsByVazamentoIdAndUsuarioId(a.getVazamentoId(), u.id())
                        || atribuicoes.findByAtendimentoIdOrderByAtribuidoEmAsc(a.getId()).stream()
                                .map(AtribuicaoEquipe::getEquipeId)
                                .anyMatch(eq -> membros.findByUsuarioIdAndFimVinculoIsNull(u.id())
                                        .map(m -> m.getEquipeId().equals(eq)).orElse(false));
            }
        }
        if (!permitido) throw ApiException.naoEncontrado("Imagem não encontrada.");   // não revela existência
        return new Conteudo(i.getTipoMime(), armazenamento.ler(i.getCaminhoArquivo()), i.getNomeOriginal());
    }

    @Transactional(readOnly = true)
    public List<Long> idsDoRelato(Long relatoId) {
        return imagens.findByRelatoId(relatoId).stream().map(Imagem::getId).toList();
    }

    @Transactional(readOnly = true)
    public List<Long> idsDoAtendimento(Long atendimentoId) {
        return imagens.findByAtendimentoId(atendimentoId).stream().map(Imagem::getId).toList();
    }
}
