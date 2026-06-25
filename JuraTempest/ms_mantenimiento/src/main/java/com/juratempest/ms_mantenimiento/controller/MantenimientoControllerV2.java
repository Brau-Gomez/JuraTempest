package com.juratempest.ms_mantenimiento.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_mantenimiento.assemblers.MantenimientoModelAssembler;
import com.juratempest.ms_mantenimiento.dto.MantenimientoDTO;
import com.juratempest.ms_mantenimiento.service.MantenimientoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/mantenimientos/v2")
public class MantenimientoControllerV2 {

    private final MantenimientoService service;
    private final MantenimientoModelAssembler assembler;

    public MantenimientoControllerV2(MantenimientoService service, MantenimientoModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MantenimientoDTO>> listar() {
        List<EntityModel<MantenimientoDTO>> mantenimientos = service.listar().stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(mantenimientos, linkTo(methodOn(MantenimientoControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<MantenimientoDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/maquina/{maquinaId}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MantenimientoDTO>> buscarPorMaquina(@PathVariable Long maquinaId) {
        List<EntityModel<MantenimientoDTO>> mantenimientos = service.buscarPorMaquina(maquinaId).stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(mantenimientos,
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorMaquina(maquinaId)).withSelfRel());
    }

    @GetMapping(value = "/estado/{estado}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MantenimientoDTO>> buscarPorEstado(@PathVariable String estado) {
        List<EntityModel<MantenimientoDTO>> mantenimientos = service.buscarPorEstado(estado).stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(mantenimientos,
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorEstado(estado)).withSelfRel());
    }

    @GetMapping(value = "/tipo/{tipo}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<MantenimientoDTO>> buscarPorTipo(@PathVariable String tipo) {
        List<EntityModel<MantenimientoDTO>> mantenimientos = service.buscarPorTipo(tipo).stream()
                .map(assembler::toModel)
                .toList();
        return CollectionModel.of(mantenimientos,
                linkTo(methodOn(MantenimientoControllerV2.class).buscarPorTipo(tipo)).withSelfRel());
    }
}
