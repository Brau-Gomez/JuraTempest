package com.juratempest.ms_eventos_torneos.controller;

import java.util.List;

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

import com.juratempest.ms_eventos_torneos.dto.InscripcionTorneoDTO;
import com.juratempest.ms_eventos_torneos.dto.TorneoDTO;
import com.juratempest.ms_eventos_torneos.service.TorneoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/torneos")
@Tag(name = "Eventos y Torneos", description = "Gestion de torneos e inscripciones de usuarios")
@Slf4j
public class TorneoController {

    private final TorneoService service;

    public TorneoController(TorneoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar torneos", description = "Retorna todos los torneos registrados")
    public ResponseEntity<List<TorneoDTO>> listar() {
        log.info("GET /torneos");
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar torneo por ID", description = "Obtiene un torneo especifico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
        @ApiResponse(responseCode = "404", description = "Torneo no encontrado")
    })
    public ResponseEntity<TorneoDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /torneos/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar torneos disponibles", description = "Retorna torneos ABIERTOS con cupos disponibles")
    public ResponseEntity<List<TorneoDTO>> listarDisponibles() {
        log.info("GET /torneos/disponibles");
        return ResponseEntity.ok(service.listarDisponibles());
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar por estado", description = "Filtra por PROGRAMADO, ABIERTO, CERRADO, FINALIZADO o CANCELADO")
    public ResponseEntity<List<TorneoDTO>> buscarPorEstado(@PathVariable String estado) {
        log.info("GET /torneos/estado/{}", estado);
        return ResponseEntity.ok(service.buscarPorEstado(estado));
    }

    @GetMapping("/{id}/inscritos")
    @Operation(summary = "Listar inscritos", description = "Lista inscripciones activas del torneo")
    public ResponseEntity<List<InscripcionTorneoDTO>> listarInscritos(@PathVariable Long id) {
        log.info("GET /torneos/{}/inscritos", id);
        return ResponseEntity.ok(service.listarInscritos(id));
    }

    @GetMapping("/usuario/{usuarioId}/inscripciones")
    @Operation(summary = "Listar inscripciones por usuario", description = "Lista inscripciones historicas de un usuario")
    public ResponseEntity<List<InscripcionTorneoDTO>> listarInscripcionesPorUsuario(@PathVariable Long usuarioId) {
        log.info("GET /torneos/usuario/{}/inscripciones", usuarioId);
        return ResponseEntity.ok(service.listarInscripcionesPorUsuario(usuarioId));
    }

    @PostMapping
    @Operation(summary = "Crear torneo", description = "Crea un torneo en estado PROGRAMADO")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Torneo creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public ResponseEntity<TorneoDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del torneo",
                    content = @Content(examples = @ExampleObject(value = "{\"nombre\":\"Copa Arcade\",\"descripcion\":\"Torneo semanal\",\"maquinaId\":1,\"horarioId\":1,\"cuposMaximos\":8}")))
            @Valid @RequestBody TorneoDTO dto) {
        log.info("POST /torneos maquinaId={} horarioId={}", dto.getMaquinaId(), dto.getHorarioId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PostMapping("/{id}/inscribir/{usuarioId}")
    @Operation(summary = "Inscribir usuario", description = "Inscribe un usuario si el torneo esta ABIERTO y quedan cupos")
    public ResponseEntity<InscripcionTorneoDTO> inscribirUsuario(@PathVariable Long id, @PathVariable Long usuarioId) {
        log.info("POST /torneos/{}/inscribir/{}", id, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.inscribirUsuario(id, usuarioId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar torneo", description = "Actualiza datos editables del torneo")
    public ResponseEntity<TorneoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody TorneoDTO dto) {
        log.info("PUT /torneos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/abrir")
    @Operation(summary = "Abrir inscripciones", description = "Cambia el torneo a ABIERTO")
    public ResponseEntity<TorneoDTO> abrir(@PathVariable Long id) {
        log.info("PUT /torneos/{}/abrir", id);
        return ResponseEntity.ok(service.abrirInscripciones(id));
    }

    @PutMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar inscripciones", description = "Cambia el torneo a CERRADO")
    public ResponseEntity<TorneoDTO> cerrar(@PathVariable Long id) {
        log.info("PUT /torneos/{}/cerrar", id);
        return ResponseEntity.ok(service.cerrarInscripciones(id));
    }

    @PutMapping("/{id}/cancelar-inscripcion/{usuarioId}")
    @Operation(summary = "Cancelar inscripcion", description = "Cancela una inscripcion activa y devuelve el cupo")
    public ResponseEntity<InscripcionTorneoDTO> cancelarInscripcion(@PathVariable Long id, @PathVariable Long usuarioId) {
        log.info("PUT /torneos/{}/cancelar-inscripcion/{}", id, usuarioId);
        return ResponseEntity.ok(service.cancelarInscripcion(id, usuarioId));
    }

    @PutMapping("/{id}/finalizar/{ganadorUsuarioId}")
    @Operation(summary = "Finalizar torneo", description = "Finaliza el torneo, asigna ganador y registra puntos")
    public ResponseEntity<TorneoDTO> finalizar(@PathVariable Long id, @PathVariable Long ganadorUsuarioId) {
        log.info("PUT /torneos/{}/finalizar/{}", id, ganadorUsuarioId);
        return ResponseEntity.ok(service.finalizar(id, ganadorUsuarioId));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar torneo", description = "Cancela el torneo si no esta finalizado")
    public ResponseEntity<TorneoDTO> cancelar(@PathVariable Long id) {
        log.info("PUT /torneos/{}/cancelar", id);
        return ResponseEntity.ok(service.cancelarTorneo(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar torneo", description = "Elimina si no tiene inscripciones activas")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        log.info("DELETE /torneos/{}", id);
        service.eliminar(id);
        return ResponseEntity.ok("Torneo eliminado correctamente");
    }
}
