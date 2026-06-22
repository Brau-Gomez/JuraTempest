package com.juratempest.ms_auth.controller;

import com.juratempest.ms_auth.dto.AuthResponseDTO;
import com.juratempest.ms_auth.dto.LoginRequestDTO;
import com.juratempest.ms_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_auth.dto.ValidacionTokenDTO;
import com.juratempest.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity.ok(authService.registrar(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidacionTokenDTO> validarToken(
        @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ResponseEntity.ok(authService.validarToken(authorization));
    }
}
