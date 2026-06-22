package com.juratempest.ms_usuarios.controller;

import com.juratempest.ms_usuarios.dto.CrearPerfilUsuarioRequestDTO;
import com.juratempest.ms_usuarios.dto.UsuarioDTO;
import com.juratempest.ms_usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
public class InternalPerfilUsuarioController {
    private final UsuarioService usuarioService;

    public InternalPerfilUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/profiles")
    public ResponseEntity<UsuarioDTO> crearPerfil(@Valid @RequestBody CrearPerfilUsuarioRequestDTO request) {
        return ResponseEntity.status(201).body(usuarioService.crearPerfilDesdeAuth(request));
    }
}
