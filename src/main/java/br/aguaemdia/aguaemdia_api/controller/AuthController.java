//Cadastro no aplicativo 

package br.com.aguaemdia.controller;

import br.com.aguaemdia.dto.AuthDtos.RegistroRequest;
import br.com.aguaemdia.dto.AuthDtos.MensagemResponse;
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
       .body(new MensaguemResponse(
         "Cadastro realizado. Verifique seu e-mail para ativa a conta"
         ));
  }
}
