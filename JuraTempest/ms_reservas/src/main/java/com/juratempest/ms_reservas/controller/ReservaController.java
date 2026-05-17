package com.juratempest.ms_reservas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juratempest.ms_reservas.dto.ReservaDTO;
import com.juratempest.ms_reservas.service.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservas")

public class ReservaController {
    private final ReservaService reservaService;

    // Constructor usado por Spring para inyectar el servicio de reservas.
    // El controlador queda dedicado a recibir requests y delegar las reglas al service.
    public ReservaController(ReservaService reservaService){
        this.reservaService = reservaService;
    }

    // Lista todas las reservas registradas.
    // Se devuelven DTO para mantener separada la API de la entidad JPA.
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar(){
        return ResponseEntity.ok(reservaService.listar());
    }

    // Busca una reserva por id.
    // El servicio maneja el caso de id inexistente y lo transforma en error de negocio.
    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    // Lista reservas asociadas a un usuario.
    // Permite consultar historial o reservas activas de un usuario sin traer todo el sistema.
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> buscarPorUsuario(@PathVariable Long usuarioId){
        return ResponseEntity.ok(reservaService.buscarPorUsuario(usuarioId));
    }

    // Filtra reservas por estado, como ACTIVA, CANCELADA o FINALIZADA.
    // El servicio normaliza el texto para consultar de forma consistente.
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaDTO>> buscarPorEstado(
        @PathVariable String estado){

        return ResponseEntity.ok(
            reservaService.buscarPorEstado(estado)
        );
    }

    // Devuelve el total de reservas registradas.
    // Usamos Map para responder JSON con una clave clara en vez de un numero aislado.
    @GetMapping("/total")
    public ResponseEntity<Map<String,Long>> totalReservas(){

        return ResponseEntity.ok(
            Map.of("total", reservaService.totalReservas())
        );
    }

    // Crea una reserva nueva validando primero el DTO.
    // La logica de validar usuario, maquina y horario se mantiene en el servicio.
    @PostMapping
    public ResponseEntity<ReservaDTO> crear(
        @Valid @RequestBody ReservaDTO dto){

        return ResponseEntity.ok(
            reservaService.crear(dto)
        );
    }

    // Actualiza una reserva existente identificada por id.
    // El cuerpo trae los nuevos datos y el service controla conflictos de horario.
    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody ReservaDTO dto){

        return ResponseEntity.ok(
            reservaService.actualizar(id, dto)
        );
    }

    // Elimina una reserva por id.
    // La existencia se valida en el servicio para responder correctamente si no se encuentra.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
        @PathVariable Long id){

        reservaService.eliminar(id);

        return ResponseEntity.ok(
            "Reserva eliminada correctamente"
        );
    }
}
