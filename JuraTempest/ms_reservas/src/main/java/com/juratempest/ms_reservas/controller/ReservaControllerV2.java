package com.juratempest.ms_reservas.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_reservas.assemblers.ReservaModelAssembler;
import com.juratempest.ms_reservas.dto.ReservaDTO;
import com.juratempest.ms_reservas.service.ReservaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/reservas/v2")
public class ReservaControllerV2 {

    private final ReservaService service;
    private final ReservaModelAssembler assembler;

    public ReservaControllerV2(ReservaService service, ReservaModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<ReservaDTO>> listar() {
        List<EntityModel<ReservaDTO>> reservas = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(reservas, linkTo(methodOn(ReservaControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<ReservaDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<ReservaDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<ReservaDTO>> reservas = service.buscarPorUsuario(usuarioId).stream().map(assembler::toModel).toList();
        return CollectionModel.of(reservas, linkTo(methodOn(ReservaControllerV2.class).buscarPorUsuario(usuarioId)).withSelfRel());
    }

    @GetMapping(value = "/estado/{estado}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<ReservaDTO>> buscarPorEstado(@PathVariable String estado) {
        List<EntityModel<ReservaDTO>> reservas = service.buscarPorEstado(estado).stream().map(assembler::toModel).toList();
        return CollectionModel.of(reservas, linkTo(methodOn(ReservaControllerV2.class).buscarPorEstado(estado)).withSelfRel());
    }
}
