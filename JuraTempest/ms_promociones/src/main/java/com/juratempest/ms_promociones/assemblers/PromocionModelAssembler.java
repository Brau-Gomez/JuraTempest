package com.juratempest.ms_promociones.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_promociones.controller.PromocionControllerV2;
import com.juratempest.ms_promociones.dto.PromocionDTO;

@Component
public class PromocionModelAssembler implements RepresentationModelAssembler<PromocionDTO, EntityModel<PromocionDTO>> {

    @Override
    public EntityModel<PromocionDTO> toModel(PromocionDTO promocion) {
        return EntityModel.of(promocion,
                linkTo(methodOn(PromocionControllerV2.class).buscarPorId(promocion.getId())).withSelfRel(),
                linkTo(methodOn(PromocionControllerV2.class).listar()).withRel("promociones"),
                linkTo(methodOn(PromocionControllerV2.class).buscarPorCodigo(promocion.getCodigo())).withRel("codigo"),
                linkTo(methodOn(PromocionControllerV2.class).buscarPorTipo(promocion.getTipo())).withRel("tipo"));
    }
}
