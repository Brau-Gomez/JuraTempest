package com.juratempest.ms_maquinas.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_maquinas.dto.MaquinaDTO;
import com.juratempest.ms_maquinas.service.MaquinaService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/maquinas")
public class MaquinaController {

    private final MaquinaService maquinaService;

    public MaquinaController(MaquinaService maquinaService){
        this.maquinaService = maquinaService;
    }
    //MAPEO GET
    @GetMapping
    public ResponseEntity<List<MaquinaDTO>> listar(){
        return ResponseEntity.ok(maquinaService.listar());
    }
    @GetMapping("/{id}")
    public ResponseEntity<MaquinaDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(maquinaService.buscarPorId(id));
    }
    @GetMapping("/{id}}/existe")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id){
        return ResponseEntity.ok(maquinaService.existePorId(id));
    }
    @GetMapping("{id}/activa")
    public ResponseEntity<Boolean> estaActivo(@PathVariable Long id){
        return ResponseEntity.ok(maquinaService.estaActiva(id));
    }
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MaquinaDTO>> buscarPorEstado(@PathVariable String estado){
        return ResponseEntity.ok(maquinaService.buscarPorEstado(estado));
    }
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MaquinaDTO>> buscarPorTipo(@PathVariable String tipo){
        return ResponseEntity.ok(maquinaService.buscarPorTipo(tipo));
    }
    @GetMapping("/total")
    public ResponseEntity<Map<String,Long>> totalMaquinas(){
        return ResponseEntity.ok(Map.of("total", maquinaService.totalMaquinas()));
    }
    
    //MAPEO POST
    @PostMapping
    public ResponseEntity<MaquinaDTO> crear(@Valid @RequestBody MaquinaDTO dto){
        return ResponseEntity.ok(maquinaService.crear(dto));
    }

    //MAPEO PUT
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<MaquinaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MaquinaDTO dto){
        return ResponseEntity.ok(maquinaService.actualizar(id, dto));
    }

    //MAPEO DELETE
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id){
        maquinaService.eliminar(id);
        return ResponseEntity.ok("Maquina eliminada con exito");
    }

    
}   
