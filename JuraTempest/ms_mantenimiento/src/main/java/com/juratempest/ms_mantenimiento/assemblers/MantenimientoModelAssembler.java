package com.juratempest.ms_mantenimiento.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_mantenimiento.controller.MantenimientoControllerV2;
import com.juratempest.ms_mantenimiento.dto.MantenimientoDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MantenimientoModelAssembler implements RepresentationModelAssembler<MantenimientoDTO, EntityModel<MantenimientoDTO>> {

    @Override
    public EntityModel<MantenimientoDTO> toModel(MantenimientoDTO mantenimiento) {
        return EntityModel.of(mantenimiento,
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorId(mantenimiento.getId())).withSelfRel(),
                linkTo(methodOn(MantenimientoControllerV2.class).listar()).withRel("mantenimientos"),
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorMaquina(mantenimiento.getMaquinaId())).withRel("maquina"),
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorEstado(mantenimiento.getEstado())).withRel("estado"),
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorTipo(mantenimiento.getTipo())).withRel("tipo"));
    }
}
