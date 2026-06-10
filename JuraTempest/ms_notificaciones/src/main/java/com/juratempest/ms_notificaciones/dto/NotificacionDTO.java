package com.juratempest.ms_notificaciones.dto;

import java.time.LocalDateTime;

import com.juratempest.ms_notificaciones.model.Notificacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionDTO {

    private Long id;

    @NotNull(message = "El id de usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El titulo es obligatorio")
    private String titulo;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "El canal es obligatorio")
    private String canal;
    @NotNull(message = "El estado es obligatorio")
    private Boolean leida;
    @NotNull(message = "La fecha de creacion es obligatoria")
    private LocalDateTime fechaCreacion;

    public Notificacion toModel() {
        Notificacion notificacion = new Notificacion();
        notificacion.setId(id);
        notificacion.setUsuarioId(usuarioId);
        notificacion.setTipo(tipo);
        notificacion.setTitulo(titulo);
        notificacion.setMensaje(mensaje);
        notificacion.setCanal(canal);
        notificacion.setLeida(leida);
        notificacion.setFechaCreacion(fechaCreacion);
        return notificacion;
    }

    public static NotificacionDTO fromModel(Notificacion notificacion) {
        if (notificacion == null) return null;
        return NotificacionDTO.builder()
            .id(notificacion.getId())
            .usuarioId(notificacion.getUsuarioId())
            .tipo(notificacion.getTipo())
            .titulo(notificacion.getTitulo())
            .mensaje(notificacion.getMensaje())
            .canal(notificacion.getCanal())
            .leida(notificacion.getLeida())
            .fechaCreacion(notificacion.getFechaCreacion())
            .build();
    }
}
