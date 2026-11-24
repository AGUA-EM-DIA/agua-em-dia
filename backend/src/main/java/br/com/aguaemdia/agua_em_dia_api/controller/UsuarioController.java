package br.com.aguaemdia.agua_em_dia_api.controller;

import br.com.aguaemdia.agua_em_dia_api.dto.EstatisticasDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.UsuarioCadastroDTO;
import br.com.aguaemdia.agua_em_dia_api.dto.UsuarioRespostaDTO;
import br.com.aguaemdia.agua_em_dia_api.entity.Usuario;
import br.com.aguaemdia.agua_em_dia_api.service.EstatisticaService;
import br.com.aguaemdia.agua_em_dia_api.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EstatisticaService estatisticaService;

    public UsuarioController(UsuarioService usuarioService, EstatisticaService estatisticaService){
        this.usuarioService = usuarioService;
        this.estatisticaService = estatisticaService;
    }

    //Edpoint para cadastras o usuário

    @PostMapping
    public ResponseEntity<Map<String, Object>> cadastrar(
            @Valid @RequestBody UsuarioCadastroDTO dados) {

        Usuario usuario = usuarioService.cadastrar(dados);
        Map<String, Object> resposta = Map.of(
                "mensagem", "Usuário cadastrado com sucesso.",
                "id", usuario.getId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    //Dados do cidadão autenticado (o id vem do token, nunca do pedido)
    @GetMapping("/me")
    public UsuarioRespostaDTO meusDados(@AuthenticationPrincipal Jwt jwt) {
        return UsuarioRespostaDTO.de(usuarioService.buscar(Long.valueOf(jwt.getSubject())));
    }

    //Estatísticas pessoais e conquistas
    @GetMapping("/me/estatisticas")
    public EstatisticasDTO minhasEstatisticas(@AuthenticationPrincipal Jwt jwt) {
        return estatisticaService.doUsuario(Long.valueOf(jwt.getSubject()));
    }

}
