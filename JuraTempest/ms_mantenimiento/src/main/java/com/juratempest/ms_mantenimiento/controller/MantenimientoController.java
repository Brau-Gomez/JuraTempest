package com.juratempest.ms_mantenimiento.controller;

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

import com.juratempest.ms_mantenimiento.dto.MantenimientoDTO;
import com.juratempest.ms_mantenimiento.service.MantenimientoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/mantenimientos")
@Tag(name = "Mantenimientos", description = "Gestion de mantenimientos de maquinas arcade")
@Slf4j
public class MantenimientoController {

    private final MantenimientoService service;

    public MantenimientoController(MantenimientoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar mantenimientos", description = "Retorna todos los mantenimientos registrados")
    @ApiResponse(responseCode = "200", description = "Operacion exitosa",
            content = @Content(schema = @Schema(implementation = MantenimientoDTO.class)))
    public ResponseEntity<List<MantenimientoDTO>> listar() {
        log.info("GET /mantenimientos");
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar mantenimiento por ID", description = "Obtiene un mantenimiento especifico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
        @ApiResponse(responseCode = "404", description = "Mantenimiento no encontrado")
    })
    public ResponseEntity<MantenimientoDTO> buscarPorId(@PathVariable Long id) {
        log.info("GET /mantenimientos/{}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/maquina/{maquinaId}")
    @Operation(summary = "Buscar por maquina", description = "Lista mantenimientos asociados a una maquina")
    public ResponseEntity<List<MantenimientoDTO>> buscarPorMaquina(@PathVariable Long maquinaId) {
        log.info("GET /mantenimientos/maquina/{}", maquinaId);
        return ResponseEntity.ok(service.buscarPorMaquina(maquinaId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar por estado", description = "Filtra por PENDIENTE, EN_PROCESO, FINALIZADO o CANCELADO")
    public ResponseEntity<List<MantenimientoDTO>> buscarPorEstado(@PathVariable String estado) {
        log.info("GET /mantenimientos/estado/{}", estado);
        return ResponseEntity.ok(service.buscarPorEstado(estado));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar por tipo", description = "Filtra por PREVENTIVO, CORRECTIVO o FALLA_REPORTADA")
    public ResponseEntity<List<MantenimientoDTO>> buscarPorTipo(@PathVariable String tipo) {
        log.info("GET /mantenimientos/tipo/{}", tipo);
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

    @PostMapping
    @Operation(summary = "Crear mantenimiento", description = "Crea un mantenimiento en estado PENDIENTE")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mantenimiento creado"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos")
    })
    public ResponseEntity<MantenimientoDTO> crear(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del mantenimiento",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "maquinaId": 1,
                              "usuarioOperadorId": 1,
                              "tipo": "PREVENTIVO",
                              "descripcion": "Limpieza preventiva de gabinete",
                              "tecnico": "Equipo Tecnico Norte",
                              "costo": 25000
                            }
                            """)))
            @Valid @RequestBody MantenimientoDTO dto) {
        log.info("POST /mantenimientos maquinaId={}", dto.getMaquinaId());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mantenimiento", description = "Actualiza datos editables manteniendo el estado actual")
    public ResponseEntity<MantenimientoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MantenimientoDTO dto) {
        log.info("PUT /mantenimientos/{}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/iniciar")
    @Operation(summary = "Iniciar mantenimiento", description = "Cambia de PENDIENTE a EN_PROCESO")
    public ResponseEntity<MantenimientoDTO> iniciar(@PathVariable Long id) {
        log.info("PUT /mantenimientos/{}/iniciar", id);
        return ResponseEntity.ok(service.iniciar(id));
    }

    @PutMapping("/{id}/cerrar")
    @Operation(summary = "Cerrar mantenimiento", description = "Cambia de EN_PROCESO a FINALIZADO")
    public ResponseEntity<MantenimientoDTO> cerrar(@PathVariable Long id) {
        log.info("PUT /mantenimientos/{}/cerrar", id);
        return ResponseEntity.ok(service.cerrar(id));
    }

    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar mantenimiento", description = "Cancela el mantenimiento si no esta finalizado")
    public ResponseEntity<MantenimientoDTO> cancelar(@PathVariable Long id) {
        log.info("PUT /mantenimientos/{}/cancelar", id);
        return ResponseEntity.ok(service.cancelar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mantenimiento", description = "Elimina si no esta EN_PROCESO ni FINALIZADO")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        log.info("DELETE /mantenimientos/{}", id);
        service.eliminar(id);
        return ResponseEntity.ok("Mantenimiento eliminado correctamente");
    }
}
