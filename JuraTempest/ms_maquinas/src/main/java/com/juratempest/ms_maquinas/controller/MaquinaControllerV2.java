package com.juratempest.ms_maquinas.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_maquinas.assemblers.MaquinaModelAssembler;
import com.juratempest.ms_maquinas.dto.MaquinaDTO;
import com.juratempest.ms_maquinas.service.MaquinaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/maquinas/v2")
public class MaquinaControllerV2 {

    private final MaquinaService service;
    private final MaquinaModelAssembler assembler;

    public MaquinaControllerV2(MaquinaService service, MaquinaModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MaquinaDTO>> listar() {
        List<EntityModel<MaquinaDTO>> maquinas = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(maquinas, linkTo(methodOn(MaquinaControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<MaquinaDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/estado/{estado}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MaquinaDTO>> buscarPorEstado(@PathVariable String estado) {
        List<EntityModel<MaquinaDTO>> maquinas = service.buscarPorEstado(estado).stream().map(assembler::toModel).toList();
        return CollectionModel.of(maquinas, linkTo(methodOn(MaquinaControllerV2.class).buscarPorEstado(estado)).withSelfRel());
    }

    @GetMapping(value = "/tipo/{tipo}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MaquinaDTO>> buscarPorTipo(@PathVariable String tipo) {
        List<EntityModel<MaquinaDTO>> maquinas = service.buscarPorTipo(tipo).stream().map(assembler::toModel).toList();
        return CollectionModel.of(maquinas, linkTo(methodOn(MaquinaControllerV2.class).buscarPorTipo(tipo)).withSelfRel());
    }
}
