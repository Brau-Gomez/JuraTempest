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

    public BloqueHorarioController(BloqueHorarioService bloqueHorarioService) {
        this.bloqueHorarioService = bloqueHorarioService;
    }

    //GET
    @GetMapping
    public ResponseEntity<List<BloquehorarioDTO>> listarBloques(){
        log.info("GET /horarios - Listando todos los bloques horarios");
        log.info("Cantidad de bloques encontrados: {}", bloqueHorarioService.listar().size());
        return ResponseEntity.ok(bloqueHorarioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloquehorarioDTO> buscarPorId(Long id){
        log.info("GET /horarios/{} - Buscando bloque horario por ID", id);
        log.info("Bloque horario encontrado: {}", bloqueHorarioService.buscarPorId(id));
        return ResponseEntity.ok(bloqueHorarioService.buscarPorId(id));
        
    }

    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id){
        log.info("GET /horarios/{}/existe - Verificando existencia de bloque horario", id);
        boolean existe = bloqueHorarioService.existePorId(id);
        log.info("Bloque horario con id={} existe: {}", id, existe);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<BloquehorarioDTO>> listarPorFecha(@PathVariable @DateTimeFormat (iso = DateTimeFormat.ISO.DATE)LocalDate fecha){
        log.info("GET /horarios/fecha/{} - Listando bloques horarios para la fecha: {}", fecha, fecha);
        return ResponseEntity.ok(bloqueHorarioService.buscarPorFecha(fecha));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<BloquehorarioDTO>> listarDisponibles(){
        log.info("GET /horarios/disponibles - Listando bloques horarios disponibles");
        log.info("Cantidad de bloques disponibles: {}", bloqueHorarioService.buscarDisponibles().size());
        return ResponseEntity.ok(bloqueHorarioService.buscarDisponibles());
    }

    @GetMapping("/rango")
    public ResponseEntity<List<BloquehorarioDTO>> listarPorRango(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                                                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin){
        log.info("GET /horarios/rango?inicio={}&fin={} - Listando bloques horarios en el rango de fechas", inicio, fin);
        return ResponseEntity.ok(bloqueHorarioService.buscarPorRango(inicio, fin));
    }

    @GetMapping("/total")
    public ResponseEntity<Long> contarBloques(){
        log.info("GET /horarios/total - Contando bloques horarios");
        return ResponseEntity.ok(bloqueHorarioService.totalBloques());
    }

    //POST, PUT, DELETE
    @PostMapping
    public ResponseEntity<BloquehorarioDTO> crear(@Valid @RequestBody BloquehorarioDTO horario){
        log.info("POST /horarios - Creando nuevo bloque de horario:{}", horario);
        BloquehorarioDTO creado = bloqueHorarioService.crear(horario);
        log.info("Bloque horario creado exitosamente: {}", creado);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloquehorarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody BloquehorarioDTO horario){
        log.info("PUT /horarios/{} - Actualizando bloque horario con datos: {}", id, horario);
        BloquehorarioDTO actualizado = bloqueHorarioService.actualizar(id, horario);
        log.info("Bloque horario actualizado exitosamente: {}", actualizado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        log.info("DELETE /horarios/{} - Eliminando bloque horario", id);
        bloqueHorarioService.eliminar(id);
        log.info("Bloque horario con id={} eliminado exitosamente", id);
        return ResponseEntity.ok("Bloque horario eliminado exitosamente");
    }
}


