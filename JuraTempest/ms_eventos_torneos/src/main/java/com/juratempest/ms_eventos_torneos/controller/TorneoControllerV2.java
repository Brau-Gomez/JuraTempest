package com.juratempest.ms_eventos_torneos.controller;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juratempest.ms_eventos_torneos.assemblers.InscripcionTorneoModelAssembler;
import com.juratempest.ms_eventos_torneos.assemblers.TorneoModelAssembler;
import com.juratempest.ms_eventos_torneos.dto.InscripcionTorneoDTO;
import com.juratempest.ms_eventos_torneos.dto.TorneoDTO;
import com.juratempest.ms_eventos_torneos.service.TorneoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/torneos/v2")
public class TorneoControllerV2 {

    private final TorneoService service;
    private final TorneoModelAssembler torneoAssembler;
    private final InscripcionTorneoModelAssembler inscripcionAssembler;

    public TorneoControllerV2(TorneoService service, TorneoModelAssembler torneoAssembler,
            InscripcionTorneoModelAssembler inscripcionAssembler) {
        this.service = service;
        this.torneoAssembler = torneoAssembler;
        this.inscripcionAssembler = inscripcionAssembler;
    }

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<TorneoDTO>> listar() {
        List<EntityModel<TorneoDTO>> torneos = service.listar().stream().map(torneoAssembler::toModel).toList();
        return CollectionModel.of(torneos, linkTo(methodOn(TorneoControllerV2.class).listar()).withSelfRel());
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    public EntityModel<TorneoDTO> buscarPorId(@PathVariable Long id) {
        return torneoAssembler.toModel(service.buscarPorId(id));
    }

    @GetMapping(value = "/disponibles", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<TorneoDTO>> listarDisponibles() {
        List<EntityModel<TorneoDTO>> torneos = service.listarDisponibles().stream().map(torneoAssembler::toModel).toList();
        return CollectionModel.of(torneos, linkTo(methodOn(TorneoControllerV2.class).listarDisponibles()).withSelfRel());
    }

    @GetMapping(value = "/estado/{estado}", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<TorneoDTO>> buscarPorEstado(@PathVariable String estado) {
        List<EntityModel<TorneoDTO>> torneos = service.buscarPorEstado(estado).stream().map(torneoAssembler::toModel).toList();
        return CollectionModel.of(torneos, linkTo(methodOn(TorneoControllerV2.class).buscarPorEstado(estado)).withSelfRel());
    }

    @GetMapping(value = "/{id}/inscritos", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<InscripcionTorneoDTO>> listarInscritos(@PathVariable Long id) {
        List<EntityModel<InscripcionTorneoDTO>> inscritos = service.listarInscritos(id).stream()
                .map(inscripcionAssembler::toModel)
                .toList();
        return CollectionModel.of(inscritos, linkTo(methodOn(TorneoControllerV2.class).listarInscritos(id)).withSelfRel());
    }

    @GetMapping(value = "/usuario/{usuarioId}/inscripciones", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<EntityModel<InscripcionTorneoDTO>> listarInscripcionesPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<InscripcionTorneoDTO>> inscripciones = service.listarInscripcionesPorUsuario(usuarioId).stream()
                .map(inscripcionAssembler::toModel)
                .toList();
        return CollectionModel.of(inscripciones,
                linkTo(methodOn(TorneoControllerV2.class).listarInscripcionesPorUsuario(usuarioId)).withSelfRel());
    }
}
