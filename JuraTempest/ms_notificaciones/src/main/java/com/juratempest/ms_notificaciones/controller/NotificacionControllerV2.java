package com.juratempest.ms_notificaciones.controller;

import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.juratempest.ms_notificaciones.assemblers.NotificacionModelAssembler;
import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.service.NotificacionService;



@RestController
@RequestMapping("/notificaciones/v2")
public class NotificacionControllerV2 {
    @Autowired
    private NotificacionService service;
    @Autowired
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




}
