package com.juratempest.ms_eventos_torneos.dto;

import java.time.LocalDate;

import com.juratempest.ms_eventos_torneos.model.Torneo;

import jakarta.validation.constraints.Min;
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
public class TorneoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "La maquina es obligatoria")
    private Long maquinaId;

    @NotNull(message = "El horario es obligatorio")
    private Long horarioId;

    @NotNull(message = "Los cupos maximos son obligatorios")
    @Min(value = 1, message = "Los cupos maximos deben ser mayores a 0")
    private Integer cuposMaximos;

    private Integer cuposDisponibles;
    private String estado;
    private Long ganadorUsuarioId;
    private LocalDate fechaCreacion;

    public Torneo toModel() {
        Torneo torneo = new Torneo();
        torneo.setId(id);
        torneo.setNombre(nombre);
        torneo.setDescripcion(descripcion);
        torneo.setMaquinaId(maquinaId);
        torneo.setHorarioId(horarioId);
        torneo.setCuposMaximos(cuposMaximos);
        torneo.setCuposDisponibles(cuposDisponibles);
        torneo.setEstado(estado);
        torneo.setGanadorUsuarioId(ganadorUsuarioId);
        torneo.setFechaCreacion(fechaCreacion);
        return torneo;
    }

    public static TorneoDTO fromModel(Torneo torneo) {
        if (torneo == null) {
            return null;
        }

        return TorneoDTO.builder()
                .id(torneo.getId())
                .nombre(torneo.getNombre())
                .descripcion(torneo.getDescripcion())
                .maquinaId(torneo.getMaquinaId())
                .horarioId(torneo.getHorarioId())
                .cuposMaximos(torneo.getCuposMaximos())
                .cuposDisponibles(torneo.getCuposDisponibles())
                .estado(torneo.getEstado())
                .ganadorUsuarioId(torneo.getGanadorUsuarioId())
                .fechaCreacion(torneo.getFechaCreacion())
                .build();
    }
}
