package com.juratempest.ms_promociones.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_promociones.assemblers.PromocionModelAssembler;
import com.juratempest.ms_promociones.dto.PromocionDTO;
import com.juratempest.ms_promociones.service.PromocionService;

@RestController
@RequestMapping("/promociones/v2")
public class PromocionControllerV2 {

    private final PromocionService service;
    private final PromocionModelAssembler assembler;

    public PromocionControllerV2(PromocionService service, PromocionModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PromocionDTO>> listar() {
        List<EntityModel<PromocionDTO>> promociones = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(promociones, linkTo(methodOn(PromocionControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<PromocionDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/codigo/{codigo}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<PromocionDTO> buscarPorCodigo(@PathVariable String codigo) {
        return assembler.toModel(service.buscarPorCodigo(codigo));
    }

    @GetMapping(value = "/vigentes", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PromocionDTO>> listarVigentes() {
        List<EntityModel<PromocionDTO>> promociones = service.listarVigentes().stream().map(assembler::toModel).toList();
        return CollectionModel.of(promociones, linkTo(methodOn(PromocionControllerV2.class).listarVigentes()).withSelfRel());
    }

    @GetMapping(value = "/tipo/{tipo}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<PromocionDTO>> buscarPorTipo(@PathVariable String tipo) {
        List<EntityModel<PromocionDTO>> promociones = service.buscarPorTipo(tipo).stream().map(assembler::toModel).toList();
        return CollectionModel.of(promociones, linkTo(methodOn(PromocionControllerV2.class).buscarPorTipo(tipo)).withSelfRel());
    }
}
