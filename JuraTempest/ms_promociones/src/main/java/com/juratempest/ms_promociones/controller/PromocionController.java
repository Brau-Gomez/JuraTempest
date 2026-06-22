package com.juratempest.ms_promociones.controller;

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

import com.juratempest.ms_promociones.dto.PromocionDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionRequestDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionResponseDTO;
import com.juratempest.ms_promociones.service.PromocionService;

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
@RequestMapping("/promociones")
@Tag(name = "Promociones", description = "Operaciones relacionadas con promociones y descuentos")
public class PromocionController {

    private final PromocionService service;

    public PromocionController(PromocionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar promociones", description = "Retorna todas las promociones registradas")
    @ApiResponse(responseCode = "200", description = "Promociones listadas correctamente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class)))
    public ResponseEntity<List<PromocionDTO>> listar() {
        log.info("Controller: listando promociones");
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar promocion por ID", description = "Obtiene una promocion por su identificador")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "400", description = "ID invalido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<PromocionDTO> buscarPorId(@Parameter(description = "ID de la promocion", example = "1") @PathVariable Long id) {
        log.info("Controller: buscando promocion id={}", id);
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar promocion por codigo", description = "Obtiene una promocion usando su codigo unico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion encontrada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Codigo invalido", content = @Content),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<PromocionDTO> buscarPorCodigo(@Parameter(description = "Codigo de promocion", example = "ARCADE10") @PathVariable String codigo) {
        log.info("Controller: buscando promocion codigo={}", codigo);
        return ResponseEntity.ok(service.buscarPorCodigo(codigo));
    }

    @GetMapping("/vigentes")
    @Operation(summary = "Listar promociones vigentes", description = "Retorna promociones activas dentro de su rango de fechas")
    @ApiResponse(responseCode = "200", description = "Promociones vigentes listadas", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class)))
    public ResponseEntity<List<PromocionDTO>> listarVigentes() {
        log.info("Controller: listando promociones vigentes");
        return ResponseEntity.ok(service.listarVigentes());
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Buscar promociones por tipo", description = "Filtra promociones por GENERAL, USUARIO_FRECUENTE, HORARIO_BAJA_DEMANDA, TORNEO o FIDELIZACION")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promociones filtradas por tipo", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Tipo invalido", content = @Content)
    })
    public ResponseEntity<List<PromocionDTO>> buscarPorTipo(@Parameter(description = "Tipo de promocion", example = "GENERAL") @PathVariable String tipo) {
        log.info("Controller: buscando promociones tipo={}", tipo);
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }

    @PostMapping
    @Operation(summary = "Crear promocion", description = "Crea una nueva promocion. El codigo y tipo se normalizan a mayusculas")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Promocion creada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o codigo duplicado", content = @Content)
    })
    public ResponseEntity<PromocionDTO> crear(
            @Valid
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la promocion a crear",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PromocionDTO.class),
                            examples = @ExampleObject(value = "{\"codigo\":\"ARCADE10\",\"nombre\":\"Descuento arcade\",\"descripcion\":\"10% de descuento general\",\"porcentajeDescuento\":10,\"fechaInicio\":\"2026-06-01\",\"fechaFin\":\"2026-12-31\",\"activa\":true,\"tipo\":\"GENERAL\"}")))
            @RequestBody PromocionDTO dto) {
        log.info("Controller: creando promocion codigo={}", dto.getCodigo());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PostMapping("/validar")
    @Operation(summary = "Validar promocion", description = "Valida una promocion por codigo y calcula el descuento sobre un monto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion valida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidarPromocionResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Promocion no aplicable o datos invalidos", content = @Content),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<ValidarPromocionResponseDTO> validarPromocion(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos para validar una promocion por codigo",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidarPromocionRequestDTO.class),
                            examples = @ExampleObject(value = "{\"codigo\":\"ARCADE10\",\"usuarioId\":10,\"reservaId\":20,\"montoOriginal\":10000}")))
            @RequestBody ValidarPromocionRequestDTO request) {
        log.info("Controller: validando promocion codigo={}", request != null ? request.getCodigo() : null);
        return ResponseEntity.ok(service.validarPromocion(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar promocion", description = "Actualiza los datos de una promocion existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion actualizada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o codigo duplicado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<PromocionDTO> actualizar(
            @Parameter(description = "ID de la promocion", example = "1") @PathVariable Long id,
            @Valid @RequestBody PromocionDTO dto) {
        log.info("Controller: actualizando promocion id={}", id);
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar promocion", description = "Marca una promocion como activa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion activada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<PromocionDTO> activar(@Parameter(description = "ID de la promocion", example = "1") @PathVariable Long id) {
        log.info("Controller: activando promocion id={}", id);
        return ResponseEntity.ok(service.activar(id));
    }

    @PutMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar promocion", description = "Marca una promocion como inactiva")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion desactivada", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PromocionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<PromocionDTO> desactivar(@Parameter(description = "ID de la promocion", example = "1") @PathVariable Long id) {
        log.info("Controller: desactivando promocion id={}", id);
        return ResponseEntity.ok(service.desactivar(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar promocion", description = "Elimina una promocion por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promocion eliminada", content = @Content(mediaType = "text/plain", examples = @ExampleObject(value = "Promocion eliminada correctamente"))),
        @ApiResponse(responseCode = "404", description = "Promocion no encontrada", content = @Content)
    })
    public ResponseEntity<String> eliminar(@Parameter(description = "ID de la promocion", example = "1") @PathVariable Long id) {
        log.info("Controller: eliminando promocion id={}", id);
        service.eliminar(id);
        return ResponseEntity.ok("Promocion eliminada correctamente");
    }
}
