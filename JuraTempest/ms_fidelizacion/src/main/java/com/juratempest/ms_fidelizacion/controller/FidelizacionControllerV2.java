package com.juratempest.ms_fidelizacion.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_fidelizacion.assemblers.FidelizacionModelAssembler;
import com.juratempest.ms_fidelizacion.dto.FidelizacionDTO;
import com.juratempest.ms_fidelizacion.service.FidelizacionService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fidelizacion/v2")
public class FidelizacionControllerV2 {

    private final FidelizacionService service;
    private final FidelizacionModelAssembler assembler;

    public FidelizacionControllerV2(FidelizacionService service, FidelizacionModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<FidelizacionDTO>> listar() {
        List<EntityModel<FidelizacionDTO>> registros = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(registros, linkTo(methodOn(FidelizacionControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<FidelizacionDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/usuario/{usuarioId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<FidelizacionDTO>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<FidelizacionDTO>> registros = service.buscarPorUsuario(usuarioId).stream().map(assembler::toModel).toList();
        return CollectionModel.of(registros, linkTo(methodOn(FidelizacionControllerV2.class).buscarPorUsuario(usuarioId)).withSelfRel());
    }
}
