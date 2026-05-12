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

    public ReservaController(ReservaService reservaService){
        this.reservaService = reservaService;
    }

    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listar(){
        return ResponseEntity.ok(reservaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(reservaService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> buscarPorUsuario(@PathVariable Long usuarioId){
        return ResponseEntity.ok(reservaService.buscarPorUsuario(usuarioId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<ReservaDTO>> buscarPorEstado(
        @PathVariable String estado){

        return ResponseEntity.ok(
            reservaService.buscarPorEstado(estado)
        );
    }

    @GetMapping("/total")
    public ResponseEntity<Map<String,Long>> totalReservas(){

        return ResponseEntity.ok(
            Map.of("total", reservaService.totalReservas())
        );
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(
        @Valid @RequestBody ReservaDTO dto){

        return ResponseEntity.ok(
            reservaService.crear(dto)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaDTO> actualizar(
        @PathVariable Long id,
        @Valid @RequestBody ReservaDTO dto){

        return ResponseEntity.ok(
            reservaService.actualizar(id, dto)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
        @PathVariable Long id){

        reservaService.eliminar(id);

        return ResponseEntity.ok(
            "Reserva eliminada correctamente"
        );
    }
}
