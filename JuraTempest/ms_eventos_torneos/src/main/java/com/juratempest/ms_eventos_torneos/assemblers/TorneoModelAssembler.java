package com.juratempest.ms_eventos_torneos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_eventos_torneos.controller.TorneoControllerV2;
import com.juratempest.ms_eventos_torneos.dto.TorneoDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TorneoModelAssembler implements RepresentationModelAssembler<TorneoDTO, EntityModel<TorneoDTO>> {

    @Override
    public EntityModel<TorneoDTO> toModel(TorneoDTO torneo) {
        return EntityModel.of(torneo,
                linkTo(methodOn(TorneoControllerV2.class).buscarPorId(torneo.getId())).withSelfRel(),
                linkTo(methodOn(TorneoControllerV2.class).listar()).withRel("torneos"),
                linkTo(methodOn(TorneoControllerV2.class).listarDisponibles()).withRel("disponibles"),
                linkTo(methodOn(TorneoControllerV2.class).buscarPorEstado(torneo.getEstado())).withRel("estado"),
                linkTo(methodOn(TorneoControllerV2.class).listarInscritos(torneo.getId())).withRel("inscritos"));
    }
}
