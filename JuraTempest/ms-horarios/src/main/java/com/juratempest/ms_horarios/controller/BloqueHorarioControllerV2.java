package com.juratempest.ms_horarios.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_horarios.assemblers.BloqueHorarioModelAssembler;
import com.juratempest.ms_horarios.dto.BloquehorarioDTO;
import com.juratempest.ms_horarios.service.BloqueHorarioService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/horarios/v2")
public class BloqueHorarioControllerV2 {

    private final BloqueHorarioService service;
    private final BloqueHorarioModelAssembler assembler;

    public BloqueHorarioControllerV2(BloqueHorarioService service, BloqueHorarioModelAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<BloquehorarioDTO>> listar() {
        List<EntityModel<BloquehorarioDTO>> horarios = service.listar().stream().map(assembler::toModel).toList();
        return CollectionModel.of(horarios, linkTo(methodOn(BloqueHorarioControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<BloquehorarioDTO> buscarPorId(@PathVariable Long id) {
        return assembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/fecha/{fecha}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<BloquehorarioDTO>> listarPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<EntityModel<BloquehorarioDTO>> horarios = service.buscarPorFecha(fecha).stream().map(assembler::toModel).toList();
        return CollectionModel.of(horarios, linkTo(methodOn(BloqueHorarioControllerV2.class).listarPorFecha(fecha)).withSelfRel());
    }

    @GetMapping(value = "/disponibles", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<BloquehorarioDTO>> listarDisponibles() {
        List<EntityModel<BloquehorarioDTO>> horarios = service.buscarDisponibles().stream().map(assembler::toModel).toList();
        return CollectionModel.of(horarios, linkTo(methodOn(BloqueHorarioControllerV2.class).listarDisponibles()).withSelfRel());
    }

    @GetMapping(value = "/rango", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<BloquehorarioDTO>> listarPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<EntityModel<BloquehorarioDTO>> horarios = service.buscarPorRango(inicio, fin).stream().map(assembler::toModel).toList();
        return CollectionModel.of(horarios,
                linkTo(methodOn(BloqueHorarioControllerV2.class).listarPorRango(inicio, fin)).withSelfRel());
    }
}
