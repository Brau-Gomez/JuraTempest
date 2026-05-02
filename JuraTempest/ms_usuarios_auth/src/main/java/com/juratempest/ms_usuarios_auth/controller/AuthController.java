package com.juratempest.ms_usuarios_auth.controller;

import com.juratempest.ms_usuarios_auth.dto.AuthResponseDTO;
import com.juratempest.ms_usuarios_auth.dto.LoginRequestDTO;
import com.juratempest.ms_usuarios_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios_auth.dto.ValidacionTokenDTO;
import com.juratempest.ms_usuarios_auth.service.JwtService;
import com.juratempest.ms_usuarios_auth.service.UsuarioService;
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
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public AuthController(UsuarioService usuarioService, JwtService jwtService) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registrar(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity.ok(usuarioService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(usuarioService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<ValidacionTokenDTO> validarToken(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        boolean valido = jwtService.esTokenValido(token);
        return ResponseEntity.ok(ValidacionTokenDTO.builder()
            .valido(valido)
            .email(valido ? jwtService.obtenerEmail(token) : null)
            .roles(valido ? jwtService.obtenerRoles(token) : null)
            .build());
    }
}
