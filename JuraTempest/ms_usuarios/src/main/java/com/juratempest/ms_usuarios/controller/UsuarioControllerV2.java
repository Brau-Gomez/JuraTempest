package com.juratempest.ms_usuarios.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_usuarios.assemblers.UsuarioModelAssembler;
import com.juratempest.ms_usuarios.dto.UsuarioDTO;
import com.juratempest.ms_usuarios.service.UsuarioService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users/v2")
public class UsuarioControllerV2 {

    private final UsuarioService service;
    private final UsuarioModelAssembler assembler;

    public UsuarioControllerV2(UsuarioService service, UsuarioModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioDTO>> listar() {
        List<EntityModel<UsuarioDTO>> usuarios = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/email/{email}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        return assembler.toModel(service.buscarPorEmail(email));
    }

    @GetMapping(value = "/frecuentes", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<UsuarioDTO>> listarFrecuentes() {
        List<EntityModel<UsuarioDTO>> usuarios = service.listarFrecuentes().stream().map(assembler::toModel).toList();
        return CollectionModel.of(usuarios, linkTo(methodOn(UsuarioControllerV2.class).listarFrecuentes()).withSelfRel());
    }
}
