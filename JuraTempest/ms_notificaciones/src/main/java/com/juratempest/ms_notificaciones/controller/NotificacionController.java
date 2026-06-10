package com.juratempest.ms_notificaciones.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.service.NotificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        // TODO: Asignar el service al atributo para delegar en el las reglas de negocio.
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<NotificacionDTO>> listar() {
        // TODO: Llamar a service.listar() para obtener todas las notificaciones.
        // TODO: Retornar ResponseEntity.ok(resultado).
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> buscarPorId(@PathVariable Long id) {
        // TODO: Llamar a service.buscarPorId(id).
        // TODO: Retornar ResponseEntity.ok(resultado).
        return ResponseEntity.ok(null);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<NotificacionDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {
        // TODO: Llamar a service.buscarPorUsuario(usuarioId).
        // TODO: Retornar ResponseEntity.ok(resultado).
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/usuario/{usuarioId}/no-leidas")
    public ResponseEntity<List<NotificacionDTO>> buscarNoLeidasPorUsuario(@PathVariable Long usuarioId) {
        // TODO: Llamar a service.buscarNoLeidasPorUsuario(usuarioId).
        // TODO: Retornar ResponseEntity.ok(resultado).
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/usuario/{usuarioId}/total-no-leidas")
    public ResponseEntity<Map<String, Long>> totalNoLeidas(@PathVariable Long usuarioId) {
        // TODO: Llamar a service.totalNoLeidasPorUsuario(usuarioId).
        // TODO: Crear un Map con la clave totalNoLeidas.
        // TODO: Retornar ResponseEntity.ok(map).
        return ResponseEntity.ok(Map.of("totalNoLeidas", 0L));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<NotificacionDTO>> buscarPorTipo(@PathVariable String tipo) {
        // TODO: Llamar a service.buscarPorTipo(tipo).
        // TODO: Retornar ResponseEntity.ok(resultado).
        return ResponseEntity.ok(List.of());
    }

    @PostMapping
    public ResponseEntity<NotificacionDTO> crear(@Valid @RequestBody NotificacionDTO dto) {
        // TODO: Llamar a service.crear(dto).
        // TODO: Retornar ResponseEntity.status(HttpStatus.CREATED).body(resultado).
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<NotificacionDTO> marcarComoLeida(@PathVariable Long id) {
        // TODO: Llamar a service.marcarComoLeida(id).
        // TODO: Retornar ResponseEntity.ok(resultado).
        return ResponseEntity.ok(null);
    }

    @PutMapping("/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<String> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        // TODO: Llamar a service.marcarTodasComoLeidas(usuarioId).
        // TODO: Retornar un mensaje de confirmacion cuando todas las notificaciones queden marcadas como leidas.
        return ResponseEntity.ok("TODO: marcar todas las notificaciones como leidas");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        // TODO: Llamar a service.eliminar(id).
        // TODO: Retornar un mensaje de confirmacion cuando la notificacion sea eliminada.
        return ResponseEntity.ok("TODO: eliminar notificacion");
    }
}
