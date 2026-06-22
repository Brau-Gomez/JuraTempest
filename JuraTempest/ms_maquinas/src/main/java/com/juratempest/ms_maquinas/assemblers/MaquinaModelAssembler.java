package com.juratempest.ms_maquinas.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_maquinas.controller.MaquinaControllerV2;
import com.juratempest.ms_maquinas.dto.MaquinaDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MaquinaModelAssembler implements RepresentationModelAssembler<MaquinaDTO, EntityModel<MaquinaDTO>> {

    @Override
    public EntityModel<MaquinaDTO> toModel(MaquinaDTO maquina) {
        return EntityModel.of(maquina,
                linkTo(methodOn(MaquinaControllerV2.class).buscarPorId(maquina.getId())).withSelfRel(),
                linkTo(methodOn(MaquinaControllerV2.class).listar()).withRel("maquinas"),
                linkTo(methodOn(MaquinaControllerV2.class).buscarPorEstado(maquina.getEstado())).withRel("estado"),
                linkTo(methodOn(MaquinaControllerV2.class).buscarPorTipo(maquina.getTipo())).withRel("tipo"));
    }
}
