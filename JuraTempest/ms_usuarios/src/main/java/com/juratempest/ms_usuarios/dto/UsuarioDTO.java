package com.juratempest.ms_usuarios.dto;

import com.juratempest.ms_usuarios.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
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

    @NotNull(message = "La cuenta asociada es obligatoria")
    private Long cuentaId;

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

    // Convierte el DTO a entidad Usuario cuando necesitamos pasar datos hacia la capa de persistencia.
    public Usuario toModel() {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setCuentaId(cuentaId);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setFrecuente(frecuente);
        usuario.setActivo(activo);
        usuario.setFechaRegistro(fechaRegistro);
        return usuario;
    }

    // Convierte una entidad Usuario a DTO para responder al cliente sin exponer campos sensibles como password.
    public static UsuarioDTO fromModel(Usuario usuario) {
        if (usuario == null) return null;
        return UsuarioDTO.builder()
            .id(usuario.getId())
            .cuentaId(usuario.getCuentaId())
            .nombre(usuario.getNombre())
            .apellido(usuario.getApellido())
            .email(usuario.getEmail())
            .frecuente(usuario.getFrecuente())
            .activo(usuario.getActivo())
            .fechaRegistro(usuario.getFechaRegistro())
            .build();
    }
}
