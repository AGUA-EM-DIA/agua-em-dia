package br.com.aguaemdia.agua_em_dia_api.controller;

import br.com.aguaemdia.agua_em_dia_api.dto.GoogleCompletarDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.GoogleCredencialDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.GoogleRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.LoginDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.LoginRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.RecuperacaoRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.RecuperacaoSolicitacaoDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.RedefinicaoSenhaDTO;
import br.com.aguaemdia.agua_em_dia_api.service.AutenticacaoService;
import br.com.aguaemdia.agua_em_dia_api.service.GoogleAuthService;
import br.com.aguaemdia.agua_em_dia_api.service.RecuperacaoSenhaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Rotas públicas de acesso: login, recuperação de senha e login com Google. */
@RestController
@RequestMapping("/api/auth")
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final RecuperacaoSenhaService recuperacaoService;
    private final GoogleAuthService googleService;

    public AutenticacaoController(AutenticacaoService autenticacaoService, RecuperacaoSenhaService recuperacaoService,
                                  GoogleAuthService googleService) {
        this.autenticacaoService = autenticacaoService;
        this.recuperacaoService = recuperacaoService;
        this.googleService = googleService;
    }

    @PostMapping("/login")
    public LoginRespostaDTO login(@Valid @RequestBody LoginDTO dados) {
        return autenticacaoService.login(dados);
    }

    @PostMapping("/recuperacao")
    public RecuperacaoRespostaDTO pedirCodigo(@Valid @RequestBody RecuperacaoSolicitacaoDTO dados) {
        return recuperacaoService.solicitar(dados.email());
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Map<String, String>> redefinirSenha(@Valid @RequestBody RedefinicaoSenhaDTO dados) {
        recuperacaoService.redefinir(dados);
        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida. Entre com a nova senha."));
    }

    @PostMapping("/google")
    public GoogleRespostaDTO entrarComGoogle(@Valid @RequestBody GoogleCredencialDTO dados) {
        return googleService.entrar(dados.credential());
    }

    @PostMapping("/google/completar")
    public LoginRespostaDTO completarCadastroGoogle(@Valid @RequestBody GoogleCompletarDTO dados) {
        return googleService.completar(dados);
    }
}
