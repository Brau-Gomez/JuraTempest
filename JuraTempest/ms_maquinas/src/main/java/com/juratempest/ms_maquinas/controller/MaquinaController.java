package com.juratempest.ms_maquinas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_maquinas.dto.MaquinaDTO;
import com.juratempest.ms_maquinas.service.MaquinaService;


import jakarta.validation.Valid;


@RestController
@RequestMapping("/maquinas")
public class MaquinaController {

    private final MaquinaService maquinaService;

    // Constructor usado por Spring para inyectar el servicio de maquinas.
    // Asi el controlador solo traduce peticiones HTTP y el service concentra reglas de negocio.
    public MaquinaController(MaquinaService maquinaService){
        this.maquinaService = maquinaService;
    }

    // Lista todas las maquinas registradas como DTO.
    // Devolvemos DTO para no acoplar la respuesta HTTP directamente a la entidad JPA.
    @GetMapping
    public ResponseEntity<List<MaquinaDTO>> listar(){
        return ResponseEntity.ok(maquinaService.listar());
    }

    // Busca una maquina por id.
    // El servicio se encarga de lanzar la excepcion si el id no existe.
    @GetMapping("/{id}")
    public ResponseEntity<MaquinaDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(maquinaService.buscarPorId(id));
    }

    // Verifica existencia por id sin devolver todos los datos de la maquina.
    // Este endpoint es util para validaciones rapidas desde otros microservicios.
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id){
        return ResponseEntity.ok(maquinaService.existePorId(id));
    }

    // Indica si una maquina esta en estado ACTIVA.
    // Se usa para que reservas pueda bloquear reservas sobre maquinas no disponibles.
    @GetMapping("/activa/{id}")
    public ResponseEntity<Boolean> estaActivo(@PathVariable Long id){
        return ResponseEntity.ok(maquinaService.estaActiva(id));
    }

    // Filtra maquinas por estado.
    // Delegamos la normalizacion y consulta al servicio para mantener el controlador limpio.
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MaquinaDTO>> buscarPorEstado(@PathVariable String estado){
        return ResponseEntity.ok(maquinaService.buscarPorEstado(estado));
    }

    // Filtra maquinas por tipo.
    // Permite a la API entregar grupos de maquinas sin que el cliente deba filtrar manualmente.
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MaquinaDTO>> buscarPorTipo(@PathVariable String tipo){
        return ResponseEntity.ok(maquinaService.buscarPorTipo(tipo));
    }

    // Devuelve el total de maquinas registradas.
    // Usamos Map para entregar una respuesta JSON con clave clara: {"total": valor}.
    @GetMapping("/total")
    public ResponseEntity<Map<String,Long>> totalMaquinas(){
        return ResponseEntity.ok(Map.of("total", maquinaService.totalMaquinas()));
    }
    
    // Crea una nueva maquina validando primero los datos del DTO.
    // @Valid activa las anotaciones de validacion antes de llamar al servicio.
    @PostMapping
    public ResponseEntity<MaquinaDTO> crear(@Valid @RequestBody MaquinaDTO dto){
        return ResponseEntity.ok(maquinaService.crear(dto));
    }

    // Actualiza una maquina existente segun su id.
    // El id identifica el recurso y el body trae los nuevos valores.
    @PutMapping("/{id}")
    public ResponseEntity<MaquinaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MaquinaDTO dto){
        return ResponseEntity.ok(maquinaService.actualizar(id, dto));
    }

    // Elimina una maquina por id.
    // La validacion de existencia se deja en el servicio para mantener respuestas consistentes.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        maquinaService.eliminar(id);
        return ResponseEntity.ok("Maquina eliminada con exito");
    }

    
}   
