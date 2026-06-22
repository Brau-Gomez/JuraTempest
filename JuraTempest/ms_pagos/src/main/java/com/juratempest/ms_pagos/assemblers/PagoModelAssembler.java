package com.juratempest.ms_pagos.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_pagos.controller.PagoControllerV2;
import com.juratempest.ms_pagos.dto.PagoDTO;

@Component
public class PagoModelAssembler implements RepresentationModelAssembler<PagoDTO, EntityModel<PagoDTO>> {

    @Override
    public EntityModel<PagoDTO> toModel(PagoDTO pago) {
        return EntityModel.of(pago,
                linkTo(methodOn(PagoControllerV2.class).buscarPorId(pago.getId())).withSelfRel(),
                linkTo(methodOn(PagoControllerV2.class).listar()).withRel("pagos"),
                linkTo(methodOn(PagoControllerV2.class).buscarPorUsuario(pago.getUsuarioId())).withRel("usuario"),
                linkTo(methodOn(PagoControllerV2.class).buscarPorReserva(pago.getReservaId())).withRel("reserva"),
                linkTo(methodOn(PagoControllerV2.class).buscarPorEstado(pago.getEstado())).withRel("estado"),
                linkTo(methodOn(PagoControllerV2.class).buscarPorMetodoPago(pago.getMetodoPago())).withRel("metodoPago"));

    }
}
