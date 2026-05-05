package com.juratempest.ms_usuarios_auth.controller;

import com.juratempest.ms_usuarios_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios_auth.dto.UsuarioDTO;
import com.juratempest.ms_usuarios_auth.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.existePorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioDTO>> buscarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }

    @GetMapping("/frecuentes")
    public ResponseEntity<List<UsuarioDTO>> listarFrecuentes() {
        return ResponseEntity.ok(usuarioService.listarFrecuentes());
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String, Long>> totalUsuarios() {
        return ResponseEntity.ok(Map.of("total", usuarioService.totalUsuarios()));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity.status(201).body(usuarioService.crearDesdeAdmin(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok("Se ha eliminado correctamente");
    }
}
