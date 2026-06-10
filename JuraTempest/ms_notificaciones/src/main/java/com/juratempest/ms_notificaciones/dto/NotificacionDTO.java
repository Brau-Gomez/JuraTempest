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

    private Boolean leida;
    private LocalDateTime fechaCreacion;

    public Notificacion toModel() {
        // TODO: Crear una entidad Notificacion usando los campos recibidos en este DTO.
        // TODO: Mapear id, usuarioId, titulo, mensaje, tipo, canal, leida y fechaCreacion.
        // TODO: Retornar la entidad para que el service pueda persistirla con el repository.
        return null;
    }

    public static NotificacionDTO fromModel(Notificacion notificacion) {
        // TODO: Validar si notificacion es null; si lo es, retornar null.
        // TODO: Crear un NotificacionDTO a partir de la entidad recibida.
        // TODO: Mapear id, usuarioId, titulo, mensaje, tipo, canal, leida y fechaCreacion.
        // TODO: Retornar el DTO para responder desde la API sin exponer directamente la entidad JPA.
        return null;
    }
}
