package com.juratempest.ms_usuarios.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.juratempest.ms_usuarios.controller.UsuarioControllerV2;
import com.juratempest.ms_usuarios.dto.UsuarioDTO;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<UsuarioDTO, EntityModel<UsuarioDTO>> {

    @Override
    public EntityModel<UsuarioDTO> toModel(UsuarioDTO usuario) {
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioControllerV2.class).buscarPorId(usuario.getId())).withSelfRel(),
                linkTo(methodOn(UsuarioControllerV2.class).listar()).withRel("usuarios"),
                linkTo(methodOn(UsuarioControllerV2.class).buscarPorEmail(usuario.getEmail())).withRel("email"),
                linkTo(methodOn(UsuarioControllerV2.class).listarFrecuentes()).withRel("frecuentes"));
    }
}
