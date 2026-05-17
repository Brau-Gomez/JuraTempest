package com.juratempest.ms_horarios.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_horarios.dto.BloquehorarioDTO;
import com.juratempest.ms_horarios.service.BloqueHorarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/horarios")
public class BloqueHorarioController {

    private static final Logger log = LoggerFactory.getLogger(BloqueHorarioController.class);
    private final BloqueHorarioService bloqueHorarioService;

    // Constructor usado por Spring para inyectar el servicio de bloques horarios.
    // El controlador se mantiene enfocado en HTTP y delega validaciones al service.
    public BloqueHorarioController(BloqueHorarioService bloqueHorarioService) {
        this.bloqueHorarioService = bloqueHorarioService;
    }

    // Lista todos los bloques horarios.
    // Se registra informacion en logs para facilitar seguimiento durante pruebas o ejecucion.
    @GetMapping
    public ResponseEntity<List<BloquehorarioDTO>> listarBloques(){
        log.info("GET /horarios - Listando todos los bloques horarios");
        log.info("Cantidad de bloques encontrados: {}", bloqueHorarioService.listar().size());
        return ResponseEntity.ok(bloqueHorarioService.listar());
    }

    // Busca un bloque horario por id.
    // El servicio resuelve el caso no encontrado para mantener consistente la respuesta de error.
    @GetMapping("/{id}")
    public ResponseEntity<BloquehorarioDTO> buscarPorId(@PathVariable Long id){
        log.info("GET /horarios/{} - Buscando bloque horario por ID", id);
        log.info("Bloque horario encontrado: {}", bloqueHorarioService.buscarPorId(id));
        return ResponseEntity.ok(bloqueHorarioService.buscarPorId(id));
        
    }

    // Verifica si existe un bloque horario por id.
    // Otros microservicios, como reservas, pueden usarlo para validar referencias antes de guardar.
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id){
        log.info("GET /horarios/{}/existe - Verificando existencia de bloque horario", id);
        boolean existe = bloqueHorarioService.existePorId(id);
        log.info("Bloque horario con id={} existe: {}", id, existe);
        return ResponseEntity.ok(existe);
    }

    // Lista bloques horarios para una fecha especifica.
    // @DateTimeFormat permite convertir el texto de la URL en LocalDate correctamente.
    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<BloquehorarioDTO>> listarPorFecha(@PathVariable @DateTimeFormat (iso = DateTimeFormat.ISO.DATE)LocalDate fecha){
        log.info("GET /horarios/fecha/{} - Listando bloques horarios para la fecha: {}", fecha, fecha);
        return ResponseEntity.ok(bloqueHorarioService.buscarPorFecha(fecha));
    }

    // Lista solo los bloques marcados como disponibles.
    // Este endpoint evita que el cliente filtre manualmente horarios no reservables.
    @GetMapping("/disponibles")
    public ResponseEntity<List<BloquehorarioDTO>> listarDisponibles(){
        log.info("GET /horarios/disponibles - Listando bloques horarios disponibles");
        log.info("Cantidad de bloques disponibles: {}", bloqueHorarioService.buscarDisponibles().size());
        return ResponseEntity.ok(bloqueHorarioService.buscarDisponibles());
    }

    // Lista bloques dentro de un rango de fechas recibido por query params.
    // Usamos @RequestParam porque inicio y fin son filtros, no identificadores del recurso.
    @GetMapping("/rango")
    public ResponseEntity<List<BloquehorarioDTO>> listarPorRango(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin){
        log.info("GET /horarios/rango?inicio={}&fin={} - Listando bloques horarios en el rango de fechas", inicio, fin);
        return ResponseEntity.ok(bloqueHorarioService.buscarPorRango(inicio, fin));
    }

    // Devuelve la cantidad total de bloques horarios.
    // El conteo se delega al servicio y repositorio para que lo haga la base de datos.
    @GetMapping("/total")
    public ResponseEntity<Long> contarBloques(){
        log.info("GET /horarios/total - Contando bloques horarios");
        return ResponseEntity.ok(bloqueHorarioService.totalBloques());
    }

    // Crea un nuevo bloque horario validando el DTO.
    // El servicio controla reglas como rango de horas, cupos y solapamiento con otros bloques.
    @PostMapping
    public ResponseEntity<BloquehorarioDTO> crear(@Valid @RequestBody BloquehorarioDTO horario){
        log.info("POST /horarios - Creando nuevo bloque de horario:{}", horario);
        BloquehorarioDTO creado = bloqueHorarioService.crear(horario, null);
        log.info("Bloque horario creado exitosamente: {}", creado);
        return ResponseEntity.ok(creado);
    }

    // Actualiza un bloque horario existente.
    // El id identifica el bloque y el body trae los datos modificados.
    @PutMapping("/{id}")
    public ResponseEntity<BloquehorarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody BloquehorarioDTO horario){
        log.info("PUT /horarios/{} - Actualizando bloque horario con datos: {}", id, horario);
        BloquehorarioDTO actualizado = bloqueHorarioService.actualizar(id, horario);
        log.info("Bloque horario actualizado exitosamente: {}", actualizado);
        return ResponseEntity.ok(actualizado);
    }

    // Elimina un bloque horario por id.
    // La existencia se valida en el servicio para responder 404 si el bloque no existe.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        log.info("DELETE /horarios/{} - Eliminando bloque horario", id);
        bloqueHorarioService.eliminar(id);
        log.info("Bloque horario con id={} eliminado exitosamente", id);
        return ResponseEntity.ok("Bloque horario eliminado exitosamente");
    }
}


