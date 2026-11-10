//CADASTRO DOS USUÁRIOS NO SISTEMA
/**
 * Controller responsável pelos endpoints de autenticação.
 *
 * RF001 - Cadastro de cidadãos.
 * RF002 - Autenticação de usuários.
 */

package br.com.aguaemdia.controller;

import br.com.aguaemdia.dto.AuthDtos.RegistroRequest;
import br.com.aguaemdia.dto.AuthDtos.MensagemResponse;
import br.com.aguaemdia.dto.AuthDtos.LoginRequest;
import br.com.aguaemdia.dto.AuthDtos.LoginResponse;
import br.com.aguaemdia.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

  
public class AuthController{
  private final AuthService authService;

  public AuthController(AuthService authService){
    this.authService = authService;
  }

@PostMapping("/registro")
  public ResponseEntity<MensagemResponse> registrar(
        @Valid @RequestBody RegistroRequest request) {

  authService.registrar(request);

  return ResponseEntity
       .status(HttpStatus.CREATED)
       .body(new MensagemResponse(
         "Cadastro realizado. Verifique seu e-mail para ativar a conta."
         ));

         
  }

  /**
 * RF002 - Autentica o usuário utilizando e-mail e senha.
 *
 * @param request dados de autenticação recebidos do cliente
 * @return token JWT e informações resumidas do usuário
 */
@PostMapping("/login")
public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody LoginRequest request) {

    LoginResponse resposta = authService.login(request);

    return ResponseEntity.ok(resposta);
}
}
