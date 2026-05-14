package com.juratempest.ms_fidelizacion.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juratempest.ms_fidelizacion.dto.FidelizacionDTO;
import com.juratempest.ms_fidelizacion.service.FidelizacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/fidelizacion")
public class FidelizacionController {

    private final FidelizacionService service;

    public FidelizacionController(FidelizacionService service) {
        this.service = service;
    }

    // GET TODOS
    @GetMapping
    public ResponseEntity<List<FidelizacionDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    // GET POR ID
    @GetMapping("/{id}")
    public ResponseEntity<FidelizacionDTO> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // GET POR USUARIO
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FidelizacionDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    // GET TOTAL PUNTOS
    @GetMapping("/total/{usuarioId}")
    public ResponseEntity<Map<String, Long>> totalPuntos(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                Map.of("total de puntos acumulados", service.totalPuntos(usuarioId))
        );
    }

    // POST
    @PostMapping
    public ResponseEntity<FidelizacionDTO> crear(
            @Valid @RequestBody FidelizacionDTO dto) {

        return ResponseEntity.ok(service.crear(dto));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.ok("Registro eliminado correctamente");
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<FidelizacionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody FidelizacionDTO dto){
        return ResponseEntity.ok(service.actualizar(id, dto));

    }
}