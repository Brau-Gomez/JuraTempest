package com.juratempest.ms_notificaciones.assemblers;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;



import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_notificaciones.controller.NotificacionControllerV2;
import com.juratempest.ms_notificaciones.dto.NotificacionDTO;

@Component
public class NotificacionModelAssembler implements RepresentationModelAssembler<NotificacionDTO, EntityModel<NotificacionDTO>> {

    @Override
    public EntityModel<NotificacionDTO> toModel(NotificacionDTO notificacion) {
        return EntityModel.of(notificacion,
                linkTo(methodOn(NotificacionControllerV2.class).buscarPorId(notificacion.getId())).withSelfRel(),
                linkTo(methodOn(NotificacionControllerV2.class).listar()).withRel("notificaciones"),
                linkTo(methodOn(NotificacionControllerV2.class).buscarPorUsuario(notificacion.getUsuarioId())).withRel("usuario")
               );
    }
}