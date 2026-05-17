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

    // Constructor usado por Spring para inyectar el servicio de fidelizacion.
    // El controlador queda enfocado en HTTP y delega reglas de negocio al service.
    public FidelizacionController(FidelizacionService service) {
        this.service = service;
    }

    // Lista todos los registros de fidelizacion como DTO.
    // Usamos ResponseEntity para devolver una respuesta HTTP clara y mantener el controller simple.
    @GetMapping
    public ResponseEntity<List<FidelizacionDTO>> listar() {

        return ResponseEntity.ok(service.listar());
    }

    // Busca un registro de fidelizacion por su id.
    // La validacion de existencia queda en el servicio para centralizar el manejo de errores.
    @GetMapping("/{id}")
    public ResponseEntity<FidelizacionDTO> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Lista los movimientos de puntos asociados a un usuario.
    // Este endpoint permite consultar el historial de fidelizacion sin cargar todos los registros.
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<FidelizacionDTO>> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(service.buscarPorUsuario(usuarioId));
    }

    // Calcula el total acumulado de puntos de un usuario.
    // Devolvemos un Map para que la respuesta JSON tenga una clave descriptiva y no sea un numero suelto.
    @GetMapping("/total/{usuarioId}")
    public ResponseEntity<Map<String, Long>> totalPuntos(
            @PathVariable Long usuarioId) {

        return ResponseEntity.ok(
                Map.of("total de puntos acumulados", service.totalPuntos(usuarioId))
        );
    }

    // Crea un nuevo movimiento de puntos.
    // @Valid activa las validaciones del DTO antes de entrar a la logica de negocio.
    @PostMapping
    public ResponseEntity<FidelizacionDTO> crear(
            @Valid @RequestBody FidelizacionDTO dto) {

        return ResponseEntity.ok(service.crear(dto));
    }

    // Elimina un registro de fidelizacion por id.
    // El servicio confirma que exista antes de eliminar para responder correctamente si no se encuentra.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {

        service.eliminar(id);

        return ResponseEntity.ok("Registro eliminado correctamente");
    }

    // Actualiza un registro de fidelizacion existente.
    // El id identifica el recurso y el DTO contiene los nuevos valores permitidos.
    @PutMapping("/{id}")
    public ResponseEntity<FidelizacionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody FidelizacionDTO dto){
        return ResponseEntity.ok(service.actualizar(id, dto));

    }
}
