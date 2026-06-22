package com.juratempest.ms_pagos.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Map;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_pagos.assemblers.PagoModelAssembler;
import com.juratempest.ms_pagos.dto.PagoDTO;
import com.juratempest.ms_pagos.service.PagoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/pagos/v2")
@Slf4j
public class PagoControllerV2 {

    private final PagoService service;
    private final PagoModelAssembler assembler;

    public PagoControllerV2(PagoService service, PagoModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PagoDTO>> listar() {
        log.info("V2 GET /pagos - Listando pagos");
        List<EntityModel<PagoDTO>> pagos = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<PagoDTO> buscarPorId(@PathVariable Long id) {
        log.info("V2 GET /pagos/{id} - Obteniendo pagos por id", id);
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PagoDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {
        log.info("V2 GET /pagos/usuario/{usuarioId} - Obteniendo pagos por ID de usuario", usuarioId);
        List<EntityModel<PagoDTO>> pagos = service.buscarPorUsuario(usuarioId).stream().map(assembler::toModel).toList();
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).buscarPorUsuario(usuarioId)).withSelfRel());
    }

    @GetMapping(value = "/reserva/{reservaId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PagoDTO>> buscarPorReserva(@PathVariable Long reservaId) {
        log.info("V2 GET /pagos/reserva/{reservaId} - Obteniendo por ID de reserva", reservaId);
        List<EntityModel<PagoDTO>> pagos = service.buscarPorReserva(reservaId).stream().map(assembler::toModel).toList();
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).buscarPorReserva(reservaId)).withSelfRel());
    }

    @GetMapping(value = "/estado/{estado}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PagoDTO>> buscarPorEstado(@PathVariable String estado) {
        log.info("V2 GET /pago/estado/{estado} - Obteniendo pagos por estado", estado);
        List<EntityModel<PagoDTO>> pagos = service.buscarPorEstado(estado).stream().map(assembler::toModel).toList();
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).buscarPorEstado(estado)).withSelfRel());
    }

    @GetMapping(value = "/metodo/{metodoPago}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PagoDTO>> buscarPorMetodoPago(@PathVariable String metodoPago){
        log.info("V2 GET /pagos/metodo/{metodoPago} - Obteniendo pagos por metodo de pago", metodoPago);
        List<EntityModel<PagoDTO>> pagos = service.buscarPorMetodoPago(metodoPago).stream().map(assembler::toModel).toList();
        return CollectionModel.of(pagos, linkTo(methodOn(PagoControllerV2.class).buscarPorMetodoPago(metodoPago)).withSelfRel());
    }

    @GetMapping(value = "/total", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<Map<String, Long>> totalPagos() {
        Map<String, Long> total = Map.of("total", service.totalPagos());
        
        return EntityModel.of(
                    total,
                    linkTo(methodOn(PagoControllerV2.class).totalPagos()).withSelfRel(),
                    linkTo(methodOn(PagoControllerV2.class).listar()).withRel("pagos")
            );
        }
}
