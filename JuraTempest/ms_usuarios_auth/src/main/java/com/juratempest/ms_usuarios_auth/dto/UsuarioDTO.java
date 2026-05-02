package com.juratempest.ms_usuarios_auth.dto;

import com.juratempest.ms_usuarios_auth.model.Rol;
import com.juratempest.ms_usuarios_auth.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;

    @NotNull(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email debe tener un formato valido")
    @NotNull(message = "El email es obligatorio")
    private String email;

    private Boolean frecuente;
    private Boolean activo;
    private LocalDate fechaRegistro;
    private Set<String> roles;

    public Usuario toModel() {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setFrecuente(frecuente);
        usuario.setActivo(activo);
        usuario.setFechaRegistro(fechaRegistro);
        return usuario;
    }

    public static UsuarioDTO fromModel(Usuario usuario) {
        if (usuario == null) return null;
        Set<String> roles = usuario.getRoles().stream()
            .map(Rol::getNombre)
            .collect(Collectors.toSet());
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .email(usuario.getEmail())
            .frecuente(usuario.getFrecuente())
            .activo(usuario.getActivo())
            .fechaRegistro(usuario.getFechaRegistro())
            .roles(roles)
            .build();
    }
}
