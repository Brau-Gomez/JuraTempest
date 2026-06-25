package com.juratempest.ms_eventos_torneos.dto;

import java.time.LocalDateTime;

import com.juratempest.ms_eventos_torneos.model.InscripcionTorneo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InscripcionTorneoDTO {
    private Long id;
    private Long torneoId;
    private Long usuarioId;
    private String estado;
    private LocalDateTime fechaInscripcion;

    public InscripcionTorneo toModel() {
        return InscripcionTorneo.builder()
                .id(id)
                .torneoId(torneoId)
                .usuarioId(usuarioId)
                .estado(estado)
                .fechaInscripcion(fechaInscripcion)
                .build();
    }

    public static InscripcionTorneoDTO fromModel(InscripcionTorneo inscripcion) {
        if (inscripcion == null) {
            return null;
        }

        return InscripcionTorneoDTO.builder()
                .id(inscripcion.getId())
                .torneoId(inscripcion.getTorneoId())
                .usuarioId(inscripcion.getUsuarioId())
                .estado(inscripcion.getEstado())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .build();
    }
}
