package com.juratempest.ms_eventos_torneos.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_eventos_torneos.controller.TorneoControllerV2;
import com.juratempest.ms_eventos_torneos.dto.InscripcionTorneoDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InscripcionTorneoModelAssembler implements RepresentationModelAssembler<InscripcionTorneoDTO, EntityModel<InscripcionTorneoDTO>> {

    @Override
    public EntityModel<InscripcionTorneoDTO> toModel(InscripcionTorneoDTO inscripcion) {
        return EntityModel.of(inscripcion,
                linkTo(methodOn(TorneoControllerV2.class).buscarPorId(inscripcion.getTorneoId())).withRel("torneo"),
                linkTo(methodOn(TorneoControllerV2.class).listarInscritos(inscripcion.getTorneoId())).withRel("inscritos"),
                linkTo(methodOn(TorneoControllerV2.class).listarInscripcionesPorUsuario(inscripcion.getUsuarioId())).withRel("usuario"));
    }
}
