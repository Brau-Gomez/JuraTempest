package com.juratempest.ms_horarios.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_horarios.controller.BloqueHorarioControllerV2;
import com.juratempest.ms_horarios.dto.BloquehorarioDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class BloqueHorarioModelAssembler implements RepresentationModelAssembler<BloquehorarioDTO, EntityModel<BloquehorarioDTO>> {

    @Override
    public EntityModel<BloquehorarioDTO> toModel(BloquehorarioDTO horario) {
        return EntityModel.of(horario,
                linkTo(methodOn(BloqueHorarioControllerV2.class).buscarPorId(horario.getId())).withSelfRel(),
                linkTo(methodOn(BloqueHorarioControllerV2.class).listar()).withRel("horarios"),
                linkTo(methodOn(BloqueHorarioControllerV2.class).listarPorFecha(horario.getFecha())).withRel("fecha"),
                linkTo(methodOn(BloqueHorarioControllerV2.class).listarDisponibles()).withRel("disponibles"));
    }
}
