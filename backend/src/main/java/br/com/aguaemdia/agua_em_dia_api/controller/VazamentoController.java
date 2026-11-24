package br.com.aguaemdia.agua_em_dia_api.controller;

import br.com.aguaemdia.agua_em_dia_api.dto.AvaliacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.SituacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.VazamentoCadastroDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.VazamentoDescricaoDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.VazamentoRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.exception.NegocioException;
import br.com.aguaemdia.agua_em_dia_api.service.VazamentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@RestController
@RequestMapping("/api/vazamentos")
public class VazamentoController {

    private final VazamentoService vazamentoService;
    private final String chaveDeServico;
    private final boolean demo;

    public VazamentoController(VazamentoService vazamentoService,
                               @Value("${app.servico.chave:}") String chaveDeServico,
                               @Value("${app.demo:false}") boolean demo) {
        this.vazamentoService = vazamentoService;
        this.chaveDeServico = chaveDeServico;
        this.demo = demo;
    }

    /** Registra um vazamento. Corpo multipart: parte "dados" (JSON) e parte "foto" (opcional). */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<VazamentoRespostaDTO> registrar(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("dados") @Valid VazamentoCadastroDTO dados,
            @RequestPart(value = "foto", required = false) MultipartFile foto) {
        VazamentoRespostaDTO criado = vazamentoService.registrar(idDo(jwt), dados, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @GetMapping
    public List<VazamentoRespostaDTO> listar(@AuthenticationPrincipal Jwt jwt) {
        return vazamentoService.listarTodos(idDo(jwt));
    }

    @GetMapping("/meus")
    public List<VazamentoRespostaDTO> listarMeus(@AuthenticationPrincipal Jwt jwt) {
        return vazamentoService.listarDoUsuario(idDo(jwt));
    }

    @GetMapping("/{id}")
    public VazamentoRespostaDTO detalhar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return vazamentoService.buscar(idDo(jwt), id);
    }

    @PatchMapping("/{id}/descricao")
    public VazamentoRespostaDTO editarDescricao(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                                @Valid @RequestBody VazamentoDescricaoDTO dados) {
        return vazamentoService.editarDescricao(idDo(jwt), id, dados.descricao());
    }

    @PostMapping("/{id}/apoios")
    public VazamentoRespostaDTO apoiar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return vazamentoService.apoiar(idDo(jwt), id);
    }

    @PostMapping("/{id}/avaliacao")
    public ResponseEntity<VazamentoRespostaDTO> avaliar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                                        @Valid @RequestBody AvaliacaoDTO dados) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vazamentoService.avaliar(idDo(jwt), id, dados));
    }

    @PostMapping("/meus/vistos")
    public ResponseEntity<Void> marcarVistos(@AuthenticationPrincipal Jwt jwt) {
        vazamentoService.marcarNovidadesComoVistas(idDo(jwt));
        return ResponseEntity.noContent().build();
    }

    /**
     * Atualiza a situação do vazamento (ABERTO, REPARO, RESOLVIDO). Não existe perfil de funcionário no app, então:
     * - em produção, só quem tem a chave de serviço (cabeçalho X-Chave-Servico, variável SERVICE_KEY) pode chamar;
     * - no modo demonstração (app.demo=true), qualquer cidadão logado pode, para usar o painel de operação simulado.
     */
    @PatchMapping("/{id}/situacao")
    public VazamentoRespostaDTO atualizarSituacao(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader(value = "X-Chave-Servico", required = false) String chave,
            @PathVariable Long id,
            @Valid @RequestBody SituacaoDTO dados) {
        boolean chaveConfere = chave != null && !chaveDeServico.isBlank()
                && MessageDigest.isEqual(chave.getBytes(StandardCharsets.UTF_8), chaveDeServico.getBytes(StandardCharsets.UTF_8));
        if (!chaveConfere && !(demo && jwt != null)) {
            throw jwt == null
                    ? NegocioException.naoAutenticado("Sessão inválida ou expirada. Entre novamente.")
                    : NegocioException.proibido("Você não tem permissão para alterar a situação de um vazamento.");
        }
        Long usuarioId = jwt == null ? null : Long.valueOf(jwt.getSubject());
        return vazamentoService.atualizarSituacao(id, dados.situacao(), usuarioId);
    }

    private static Long idDo(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
