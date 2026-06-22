package com.juratempest.ms_reservas.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_reservas.controller.ReservaControllerV2;
import com.juratempest.ms_reservas.dto.ReservaDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ReservaModelAssembler implements RepresentationModelAssembler<ReservaDTO, EntityModel<ReservaDTO>> {

    @Override
    public EntityModel<ReservaDTO> toModel(ReservaDTO reserva) {
        return EntityModel.of(reserva,
                linkTo(methodOn(ReservaControllerV2.class).buscarPorId(reserva.getId())).withSelfRel(),
                linkTo(methodOn(ReservaControllerV2.class).listar()).withRel("reservas"),
                linkTo(methodOn(ReservaControllerV2.class).buscarPorUsuario(reserva.getUsuarioId())).withRel("usuario"),
                linkTo(methodOn(ReservaControllerV2.class).buscarPorEstado(reserva.getEstado())).withRel("estado"));
    }
}
