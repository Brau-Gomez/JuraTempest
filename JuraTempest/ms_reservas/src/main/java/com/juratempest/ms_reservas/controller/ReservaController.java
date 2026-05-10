package com.juratempest.ms_reservas.controller;

import java.util.List;

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

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDTO>> buscarPorUsuario(
        @PathVariable Long usuarioId){

        return ResponseEntity.ok(
            reservaService.buscarPorUsuario(usuarioId)
        );
    }

    @PostMapping
    public ResponseEntity<ReservaDTO> crear(
        @Valid @RequestBody ReservaDTO dto){

        return ResponseEntity.ok(
            reservaService.crear(dto)
        );
    }
}