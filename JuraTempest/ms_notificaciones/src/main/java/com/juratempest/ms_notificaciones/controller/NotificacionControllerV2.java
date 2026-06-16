package com.juratempest.ms_notificaciones.controller;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import com.juratempest.ms_notificaciones.assemblers.NotificacionModelAssembler;
import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.service.NotificacionService;

import jakarta.validation.Valid;



@RestController
@RequestMapping("/notificaciones/v2")
public class NotificacionControllerV2 {
    @Autowired
    private NotificacionService service;
    private NotificacionModelAssembler assembler;

    
    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<NotificacionDTO>> listar(){
        List<EntityModel<NotificacionDTO>> notificaciones = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(notificaciones,
             linkTo(methodOn(NotificacionControllerV2.class).listar()).withSelfRel());
        
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<NotificacionDTO> buscarPorId(@PathVariable Long id){
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<NotificacionDTO>> buscarPorUsuario(@PathVariable Long usuarioId){
        List<EntityModel<NotificacionDTO>> notificaciones = service.buscarPorUsuario(usuarioId).stream()
        .map(assembler::toModel).toList();

        return CollectionModel.of(notificaciones, linkTo(methodOn(NotificacionControllerV2.class).buscarPorUsuario(usuarioId)).withSelfRel());
    }

    @GetMapping(value = "/usuario/{usuarioId}/no-leidas", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<NotificacionDTO>> buscarNoLeidasPorUsuario(@PathVariable Long usuarioId){
        List<EntityModel<NotificacionDTO>> noLeida = service.buscarNoLeidasPorUsuario(usuarioId).stream().map(assembler::toModel).toList();

        return CollectionModel.of(noLeida, linkTo(methodOn(NotificacionControllerV2.class).buscarNoLeidasPorUsuario(usuarioId)).withSelfRel());
    }

    @GetMapping(value = "/usuario/{usuarioId}/total-no-leidas")
    public ResponseEntity<Map<String, Long>> totalNoLeidas(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(Map.of("totalNoLeidas", service.totalNoLeidasPorUsuario(usuarioId)));
    }

    @GetMapping(value = "/tipo/{tipo}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<NotificacionDTO>> buscarPorTipo(@PathVariable String tipo) {
        List<EntityModel<NotificacionDTO>> notificaciones = service.buscarPorTipo(tipo).stream()
            .map(assembler::toModel)
            .toList();
    
        return CollectionModel.of(notificaciones,
            linkTo(methodOn(NotificacionControllerV2.class).buscarPorTipo(tipo)).withSelfRel());
    }
    
    @GetMapping(value = "/no-leidas", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<NotificacionDTO>> obtenerNoLeidas(){
        List<EntityModel<NotificacionDTO>> notificaciones = service.obtenerNoLeidas().stream().map(assembler::toModel).toList();

        return CollectionModel.of(notificaciones, linkTo(methodOn(NotificacionControllerV2.class).obtenerNoLeidas()).withSelfRel());
    }

    @PutMapping(value = "/{id}/leer", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<NotificacionDTO> marcarComoLeida(@PathVariable Long id){
        return assembler.toModel(service.marcarComoLeida(id));
    }

    @PutMapping(value = "/usuario/{usuarioId}/leer-todas")
    public ResponseEntity<String> marcarTodasComoLeidas(@PathVariable Long usuarioId) {
        service.marcarTodasComoLeidas(usuarioId);
        return ResponseEntity.ok("Notificaciones marcadas como leidas");
    }


    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity<EntityModel<NotificacionDTO>> crear(@Valid @RequestBody NotificacionDTO dto){
        NotificacionDTO nueva = service.crear(dto);

        return ResponseEntity
        .created(linkTo(methodOn(NotificacionControllerV2.class)
        .buscarPorId(nueva.getId()))
        .toUri())
        .body(assembler.toModel(nueva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
}


}
