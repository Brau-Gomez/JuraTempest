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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones", description = "Operaciones relacionadas con las notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    //LISTAR NOTIFICACIONES
    @GetMapping
    @Operation(summary = "Listar todas las notificaciones", description = "Retorna una lista de todas las notificaciones")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class)))
    })
    public ResponseEntity<List<NotificacionDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    //OBTENER NOTIFICACION POR ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener una notificacion por ID", description = "Obtiene una notificacion especifica usando su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NotificacionDTO.class))),
        @ApiResponse(responseCode = "404", description = "Notificacion no encontrada")
    })

    public ResponseEntity<NotificacionDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }


    //OBTENER NOTIFICACION POR ID DE USUARIO
    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener notificaciones por usuario", description = "Obtiene todas las notificaciones asociadas a un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NotificacionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Usuario invalido")
    })
    public ResponseEntity<List<NotificacionDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    //OBTENER NOTIFICACIONES NO LEIDAS POR ID DE USUARIO
    @GetMapping("/usuario/{usuarioId}/no-leidas")
    @Operation(summary = "Obtener notificaciones no leidas", description = "Obtiene las notificaciones no leidas de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NotificacionDTO.class)))
    })
    public ResponseEntity<List<NotificacionDTO>> buscarNoLeidasPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.buscarNoLeidasPorUsuario(usuarioId));
    }

    //OBTENER TOTAL DE NOTIFICACIONES NO LEIDAS POR ID DE USUARIO
    @GetMapping("/usuario/{usuarioId}/total-no-leidas")
    @Operation(summary = "Contar notificaciones no leidas", description = "Obtiene el total de notificaciones no leidas de un usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa")
    })
    public ResponseEntity<Map<String, Long>> totalNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(Map.of("totalNoLeidas", service.totalNoLeidasPorUsuario(usuarioId)));
    }

    //OBTENER NOTIFICACION POR TIPO DE NOTIFICACION
    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Obtener notificaciones por tipo", description = "Obtiene notificaciones filtradas por tipo: RESERVA, PAGO, MANTENIMIENTO, TORNEO, PROMOCION o SISTEMA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NotificacionDTO.class)))
    })
    public ResponseEntity<List<NotificacionDTO>> buscarPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(service.buscarPorTipo(tipo));
    }


    //OBTENER EL TOTAL NOTIFICACIONES NO LEIDAS
    @GetMapping("/no-leidas")
    @Operation(summary = "Recupera todas las notificaciones no leidas", description = "Obtiene el total de notificaciones no leidas, independiente del usuario")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificacionDTO.class)))
    })
    public ResponseEntity<List<NotificacionDTO>> buscarNoLeidas(){
        return ResponseEntity.ok(service.obtenerNoLeidas());
    }

    //POSTMAPPING
    //CREAR NOTIFICACION
    @PostMapping
    @Operation(summary = "Crear una nueva notificacion", description = "Crea una notificacion para un usuario. El tipo y canal se guardan en mayusculas automaticamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Notificacion creada exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NotificacionDTO.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<NotificacionDTO> crear(@Valid @RequestBody NotificacionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    @PutMapping("/{id}/leer")
    @Operation(summary = "Marcar notificacion como leida", description = "Marca una notificacion especifica como leida")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificacion marcada como leida",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = NotificacionDTO.class))),
        @ApiResponse(responseCode = "400", description = "La notificacion ya fue leida"),
        @ApiResponse(responseCode = "404", description = "Notificacion no encontrada")
    })
    public ResponseEntity<NotificacionDTO> marcarComoLeida(@PathVariable Long id) {
        return ResponseEntity.ok(service.marcarComoLeida(id));
    }

    @PutMapping("/usuario/{usuarioId}/leer-todas")
    @Operation(summary = "Marcar todas como leidas", description = "Marca todas las notificaciones no leidas de un usuario como leidas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificaciones marcadas como leidas"),
        @ApiResponse(responseCode = "400", description = "Usuario invalido")
    })
    public ResponseEntity<String> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        service.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok("Notificaciones marcadas como leidas");
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una notificacion", description = "Elimina una notificacion por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notificacion eliminada exitosamente"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar una notificacion no leida"),
        @ApiResponse(responseCode = "404", description = "Notificacion no encontrada")
    })
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.ok("Notificacion eliminada correctamente");
    }
}
