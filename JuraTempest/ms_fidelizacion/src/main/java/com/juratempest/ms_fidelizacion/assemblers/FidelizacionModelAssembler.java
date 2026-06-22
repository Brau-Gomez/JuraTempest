package com.juratempest.ms_fidelizacion.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_fidelizacion.controller.FidelizacionControllerV2;
import com.juratempest.ms_fidelizacion.dto.FidelizacionDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class FidelizacionModelAssembler implements RepresentationModelAssembler<FidelizacionDTO, EntityModel<FidelizacionDTO>> {

    @Override
    public EntityModel<FidelizacionDTO> toModel(FidelizacionDTO registro) {
        return EntityModel.of(registro,
                linkTo(methodOn(FidelizacionControllerV2.class).buscarPorId(registro.getId())).withSelfRel(),
                linkTo(methodOn(FidelizacionControllerV2.class).listar()).withRel("fidelizacion"),
                linkTo(methodOn(FidelizacionControllerV2.class).buscarPorUsuario(registro.getUsuarioId())).withRel("usuario"));
    }
}
