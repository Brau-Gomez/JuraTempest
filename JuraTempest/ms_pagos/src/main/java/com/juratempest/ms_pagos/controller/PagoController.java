package com.juratempest.ms_pagos.controller;

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

import com.juratempest.ms_pagos.dto.PagoDTO;
import com.juratempest.ms_pagos.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/pagos")
@Tag(name = "Pagos", description = "Operaciones relacionadas con pagos de reservas")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar pagos", description = "Retorna todos los pagos registrados")
    @ApiResponse(responseCode = "200", description = "Pagos listados correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class)))
    public ResponseEntity<List<PagoDTO>> listar() {
        log.info("Controller: listando pagos");
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pago por ID", description = "Obtiene un pago especifico por su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "ID invalido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<PagoDTO> buscarPorId(@Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        log.info("Controller: buscando pago id={}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Buscar pagos por usuario", description = "Lista los pagos asociados a un usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagos del usuario listados correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Usuario invalido", content = @Content)
    })
    public ResponseEntity<List<PagoDTO>> buscarPorUsuario(@Parameter(description = "ID del usuario", example = "10") @PathVariable Long usuarioId) {
        log.info("Controller: buscando pagos por usuarioId={}", usuarioId);
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    @GetMapping("/reserva/{reservaId}")
    @Operation(summary = "Buscar pagos por reserva", description = "Lista los pagos asociados a una reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagos de la reserva listados correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Reserva invalida", content = @Content)
    })
    public ResponseEntity<List<PagoDTO>> buscarPorReserva(@Parameter(description = "ID de la reserva", example = "20") @PathVariable Long reservaId) {
        log.info("Controller: buscando pagos por reservaId={}", reservaId);
        return ResponseEntity.ok(service.buscarPorReserva(reservaId));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Buscar pagos por estado", description = "Filtra pagos por PENDIENTE, APROBADO, RECHAZADO o ANULADO")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagos filtrados por estado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Estado invalido", content = @Content)
    })
    public ResponseEntity<List<PagoDTO>> buscarPorEstado(@Parameter(description = "Estado del pago", example = "PENDIENTE") @PathVariable String estado) {
        log.info("Controller: buscando pagos por estado={}", estado);
        return ResponseEntity.ok(service.buscarPorEstado(estado));
    }

    @GetMapping("/metodo/{metodoPago}")
    @Operation(summary = "Buscar pagos por metodo", description = "Filtra pagos por EFECTIVO, DEBITO, CREDITO o TRANSFERENCIA")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pagos filtrados por metodo de pago", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Metodo de pago invalido", content = @Content)
    })
    public ResponseEntity<List<PagoDTO>> buscarPorMetodoPago(@Parameter(description = "Metodo de pago", example = "DEBITO") @PathVariable String metodoPago) {
        log.info("Controller: buscando pagos por metodoPago={}", metodoPago);
        return ResponseEntity.ok(service.buscarPorMetodoPago(metodoPago));
    }

    @GetMapping("/total")
    @Operation(summary = "Contar pagos", description = "Retorna el total de pagos registrados")
    @ApiResponse(responseCode = "200", description = "Total de pagos retornado correctamente", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"total\": 3}")))
    public ResponseEntity<Map<String, Long>> totalPagos() {
        log.info("Controller: consultando total de pagos");
        return ResponseEntity.ok(Map.of("total", service.totalPagos()));
    }

    @PostMapping
    @Operation(summary = "Crear pago", description = "Crea un pago pendiente calculando valor neto, descuento, IVA y monto final desde la reserva")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pago creado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o regla de negocio incumplida", content = @Content),
        @ApiResponse(responseCode = "404", description = "Reserva, maquina o promocion no encontrada", content = @Content)
    })
    public ResponseEntity<PagoDTO> crear(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear un pago. Los montos, IVA, estado y fechas son calculados por el servicio.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagoDTO.class),
                            examples = @ExampleObject(value = "{\"usuarioId\":10,\"reservaId\":20,\"promocionId\":5,\"metodoPago\":\"DEBITO\"}")))
            @RequestBody PagoDTO dto) {
        log.info(
                "Controller: creando pago usuarioId={} reservaId={} metodoPago={}",
                dto.getUsuarioId(),
                dto.getReservaId(),
                dto.getMetodoPago());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar pago", description = "Actualiza metodo de pago o promocion de un pago pendiente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago actualizado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o pago no editable", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pago o referencia no encontrada", content = @Content)
    })
    public ResponseEntity<PagoDTO> actualizar(
            @Parameter(description = "ID del pago", example = "1") @PathVariable Long id,
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos actualizados del pago pendiente.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PagoDTO.class),
                            examples = @ExampleObject(value = "{\"usuarioId\":10,\"reservaId\":20,\"metodoPago\":\"CREDITO\"}")))
            @RequestBody PagoDTO dto) {
        log.info("Controller: actualizando pago id={}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/aprobar")
    @Operation(summary = "Aprobar pago", description = "Aprueba un pago pendiente, registra puntos y notifica al usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago aprobado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "El pago no esta pendiente o ya existe otro pago aprobado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<PagoDTO> aprobar(@Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        log.info("Controller: aprobando pago id={}", id);
        return ResponseEntity.ok(service.aprobar(id));
    }

    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Rechazar pago", description = "Rechaza un pago pendiente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago rechazado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "El pago no esta pendiente", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<PagoDTO> rechazar(@Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        log.info("Controller: rechazando pago id={}", id);
        return ResponseEntity.ok(service.rechazar(id));
    }

    @PutMapping("/{id}/anular")
    @Operation(summary = "Anular pago", description = "Anula un pago que no este aprobado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago anulado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PagoDTO.class))),
        @ApiResponse(responseCode = "400", description = "El pago ya fue aprobado o anulado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<PagoDTO> anular(@Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        log.info("Controller: anulando pago id={}", id);
        return ResponseEntity.ok(service.anular(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar pago", description = "Elimina un pago si la regla de negocio lo permite")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago eliminado", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Pago eliminado correctamente"))),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar un pago aprobado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Pago no encontrado", content = @Content)
    })
    public ResponseEntity<String> eliminar(@Parameter(description = "ID del pago", example = "1") @PathVariable Long id) {
        log.info("Controller: eliminando pago id={}", id);
        service.eliminar(id);
        return ResponseEntity.ok("Pago eliminado correctamente");
    }
}
