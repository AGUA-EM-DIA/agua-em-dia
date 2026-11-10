package br.com.aguaemdia.service;

import br.com.aguaemdia.common.ApiException;
import br.com.aguaemdia.common.Papeis;
import br.com.aguaemdia.common.Textos;
import br.com.aguaemdia.domain.MembroEquipe;
import br.com.aguaemdia.domain.Papel;
import br.com.aguaemdia.domain.RascunhoRelato;
import br.com.aguaemdia.domain.Usuario;
import br.com.aguaemdia.domain.UsuarioPapel;
import br.com.aguaemdia.domain.VazamentoStatus;
import br.com.aguaemdia.dto.AuthDtos.UsuarioResumo;
import br.com.aguaemdia.dto.UsuarioDtos.EstatisticasCidadao;
import br.com.aguaemdia.repository.MembroEquipeRepository;
import br.com.aguaemdia.repository.PapelRepository;
import br.com.aguaemdia.repository.RascunhoRelatoRepository;
import br.com.aguaemdia.repository.RelatoRepository;
import br.com.aguaemdia.repository.TokenRecuperacaoRepository;
import br.com.aguaemdia.repository.TokenVerificacaoRepository;
import br.com.aguaemdia.repository.UsuarioPapelRepository;
import br.com.aguaemdia.repository.UsuarioRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarios;
    private final PapelRepository papeis;
    private final UsuarioPapelRepository usuarioPapeis;
    private final MembroEquipeRepository membros;
    private final RascunhoRelatoRepository rascunhos;
    private final RelatoRepository relatos;
    private final TokenVerificacaoRepository tokensVerificacao;
    private final TokenRecuperacaoRepository tokensRecuperacao;
    private final TokenEmailService tokenEmail;
    private final AuditoriaService auditoria;
    private final PasswordEncoder encoder;
    private final Clock clock;

    public UsuarioService(UsuarioRepository usuarios, PapelRepository papeis, UsuarioPapelRepository usuarioPapeis,
            MembroEquipeRepository membros, RascunhoRelatoRepository rascunhos, RelatoRepository relatos,
            TokenVerificacaoRepository tokensVerificacao, TokenRecuperacaoRepository tokensRecuperacao,
            TokenEmailService tokenEmail, AuditoriaService auditoria, PasswordEncoder encoder, Clock clock) {
        this.usuarios = usuarios;
        this.papeis = papeis;
        this.usuarioPapeis = usuarioPapeis;
        this.membros = membros;
        this.rascunhos = rascunhos;
        this.relatos = relatos;
        this.tokensVerificacao = tokensVerificacao;
        this.tokensRecuperacao = tokensRecuperacao;
        this.tokenEmail = tokenEmail;
        this.auditoria = auditoria;
        this.encoder = encoder;
        this.clock = clock;
    }

    public static String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    /** Cria usuário com os papéis informados. situacao: PENDENTE_VERIFICACAO ou ATIVO. */
    @Transactional
    public Usuario criar(String nome, String email, String telefone, String senhaClara, String situacao,
            Collection<String> nomesPapeis, Long por) {
        String em = normalizarEmail(email);
        if (usuarios.existsByEmailIgnoreCase(em)) throw ApiException.conflito("E-mail já cadastrado.");
        Usuario u = new Usuario();
        u.setNome(Textos.truncar(nome.trim(), 150));
        u.setEmail(em);
        u.setTelefone(Textos.truncar(Textos.limpar(telefone), 30));
        u.setSenhaHash(encoder.encode(senhaClara));
        u.setSituacao(situacao);
        if ("ATIVO".equals(situacao)) u.setEmailVerificadoEm(OffsetDateTime.now(clock));
        u = usuarios.save(u);
        for (String p : nomesPapeis) concederPapel(u.getId(), p, por);
        auditoria.registrar(por, "USUARIO_CRIADO", "Usuario", u.getId(), null, Map.of("papeis", nomesPapeis));
        return u;
    }

    /** Conta criada por administrador: sem senha conhecida; o convidado define a sua pelo link enviado. */
    @Transactional
    public Usuario convidar(String nome, String email, String telefone, Collection<String> nomesPapeis, Long por) {
        Usuario u = criar(nome, email, telefone, Textos.tokenAleatorio(), "PENDENTE_VERIFICACAO", nomesPapeis, por);
        tokenEmail.enviarRecuperacao(u, true);
        return u;
    }

    @Transactional
    public void concederPapel(Long usuarioId, String nomePapel, Long por) {
        Papel p = papeis.findByNome(nomePapel).orElseThrow(() -> ApiException.invalido("Papel inexistente: " + nomePapel));
        if (usuarioPapeis.findByUsuarioIdAndPapelIdAndRevogadoEmIsNull(usuarioId, p.getId()).isPresent()) return;
        UsuarioPapel up = new UsuarioPapel();
        up.setUsuarioId(usuarioId);
        up.setPapelId(p.getId());
        up.setAtribuidoPor(por);
        usuarioPapeis.save(up);
        auditoria.registrar(por, "PAPEL_CONCEDIDO", "Usuario", usuarioId, null, Map.of("papel", nomePapel));
    }

    @Transactional
    public void revogarPapel(Long usuarioId, String nomePapel, Long por) {
        Papel p = papeis.findByNome(nomePapel).orElseThrow(() -> ApiException.invalido("Papel inexistente: " + nomePapel));
        UsuarioPapel up = usuarioPapeis.findByUsuarioIdAndPapelIdAndRevogadoEmIsNull(usuarioId, p.getId())
                .orElseThrow(() -> ApiException.naoEncontrado("O usuário não possui este papel."));
        if (Papeis.ADMINISTRADOR.equals(nomePapel) && usuarioPapeis.contarVigentes(Papeis.ADMINISTRADOR) <= 1) {
            throw ApiException.negocio("Não é possível remover o último administrador do sistema.");
        }
        up.setRevogadoEm(OffsetDateTime.now(clock));
        if (Papeis.EQUIPE.equals(nomePapel)) encerrarVinculoEquipe(usuarioId);
        auditoria.registrar(por, "PAPEL_REVOGADO", "Usuario", usuarioId, Map.of("papel", nomePapel), null);
    }

    @Transactional
    public Usuario alterarSituacao(Long id, boolean ativar, Long por) {
        Usuario u = obter(id);
        if ("ANONIMIZADO".equals(u.getSituacao())) throw ApiException.negocio("Conta anonimizada não pode ser alterada.");
        if (id.equals(por)) throw ApiException.negocio("Você não pode alterar a própria situação.");
        String anterior = u.getSituacao();
        if (ativar) {
            u.setSituacao("ATIVO");
            if (u.getEmailVerificadoEm() == null) u.setEmailVerificadoEm(OffsetDateTime.now(clock));
        } else {
            if (usuarioPapeis.nomesVigentes(id).contains(Papeis.ADMINISTRADOR) && usuarioPapeis.contarVigentes(Papeis.ADMINISTRADOR) <= 1) {
                throw ApiException.negocio("Não é possível desativar o último administrador do sistema.");
            }
            u.setSituacao("DESATIVADO");
            encerrarVinculoEquipe(id);
        }
        auditoria.registrar(por, "SITUACAO_ALTERADA", "Usuario", id, Map.of("situacao", anterior), Map.of("situacao", u.getSituacao()));
        return u;
    }

    /** LGPD (RNF004): remove dados pessoais mantendo os relatos para integridade das estatísticas. */
    @Transactional
    public void anonimizar(Long id, Long por) {
        Usuario u = obter(id);
        if ("ANONIMIZADO".equals(u.getSituacao())) return;
        if (usuarioPapeis.nomesVigentes(id).contains(Papeis.ADMINISTRADOR) && usuarioPapeis.contarVigentes(Papeis.ADMINISTRADOR) <= 1) {
            throw ApiException.negocio("Não é possível anonimizar o último administrador do sistema.");
        }
        OffsetDateTime agora = OffsetDateTime.now(clock);
        u.setNome("Usuário anonimizado");
        u.setEmail("anonimizado-" + id + "@anonimizado.invalid");
        u.setTelefone(null);
        u.setSenhaHash(encoder.encode(Textos.tokenAleatorio()));
        u.setSituacao("ANONIMIZADO");
        u.setAnonimizadoEm(agora);
        for (UsuarioPapel up : usuarioPapeis.findByUsuarioIdAndRevogadoEmIsNull(id)) up.setRevogadoEm(agora);
        encerrarVinculoEquipe(id);
        List<RascunhoRelato> meus = rascunhos.findByUsuarioIdOrderByAtualizadoEmDesc(id);
        rascunhos.deleteAll(meus);
        tokensVerificacao.invalidarPendentes(id, agora);
        tokensRecuperacao.invalidarPendentes(id, agora);
        auditoria.registrar(por, "USUARIO_ANONIMIZADO", "Usuario", id, null, null);
    }

    @Transactional
    public void anonimizarProprio(Long id, String senha) {
        Usuario u = obter(id);
        if (!encoder.matches(senha, u.getSenhaHash())) throw ApiException.proibido("Senha incorreta.");
        anonimizar(id, id);
    }

    private void encerrarVinculoEquipe(Long usuarioId) {
        membros.findByUsuarioIdAndFimVinculoIsNull(usuarioId)
                .ifPresent((MembroEquipe m) -> m.setFimVinculo(OffsetDateTime.now(clock)));
    }

    @Transactional(readOnly = true)
    public Usuario obter(Long id) {
        return usuarios.findById(id).orElseThrow(() -> ApiException.naoEncontrado("Usuário não encontrado."));
    }

    @Transactional(readOnly = true)
    public UsuarioResumo resumo(Long id) {
        Usuario u = obter(id);
        return new UsuarioResumo(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getSituacao(),
                new HashSet<>(usuarioPapeis.nomesVigentes(id)));
    }

    @Transactional
    public UsuarioResumo atualizarPerfil(Long id, String nome, String telefone) {
        Usuario u = obter(id);
        u.setNome(Textos.truncar(nome.trim(), 150));
        u.setTelefone(Textos.truncar(Textos.limpar(telefone), 30));
        return resumo(id);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResumo> listar(String busca, String situacao, Pageable pageable) {
        String q = Textos.limpar(busca) == null ? null : "%" + busca.trim().toLowerCase(Locale.ROOT) + "%";
        return usuarios.buscar(q, Textos.limpar(situacao), pageable).map(u ->
                new UsuarioResumo(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), u.getSituacao(),
                        new HashSet<>(usuarioPapeis.nomesVigentes(u.getId()))));
    }

    /** RF004: estatísticas do cidadão (sem dupla contagem de ocorrências agrupadas). */
    @Transactional(readOnly = true)
    public EstatisticasCidadao estatisticas(Long usuarioId) {
        return new EstatisticasCidadao(
                relatos.countByUsuarioId(usuarioId),
                relatos.contarVazamentosDoUsuario(usuarioId, VazamentoStatus.nomes(VazamentoStatus.ATIVOS)),
                relatos.contarAtendimentosConcluidos(usuarioId));
    }
}
