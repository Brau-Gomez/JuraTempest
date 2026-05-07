package com.juratempest.ms_horarios.dto;

import java.time.LocalDate;

import com.juratempest.ms_horarios.model.BloqueHorario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BloquehorarioDTO {
    private Long id;
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate dia;
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalDate horaInicio;
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalDate horaFin;
    @NotNull
    private boolean disponible;
    @Size(max = 30, message = "El estado no puede tener más de 30 caracteres")    private String estado;
    @NotNull
    @Min(value = 1, message = "La capacidad de la máquina debe ser al menos 1")
    private String capacidadMaquina;
    @Min(value = 0, message = "Los cupos disponibles no pueden ser negativos")
    private int cuposDisponibles;

    public BloqueHorario toModel(){
        return new BloqueHorario(id, dia, horaInicio, horaFin, disponible, estado, capacidadMaquina, cuposDisponibles);
    }

    public static BloquehorarioDTO fromModel(BloqueHorario bloque){
        if (bloque == null) return null;
        return BloquehorarioDTO.builder()
                .id(bloque.getId())
                .dia(bloque.getDia())
                .horaInicio(bloque.getHoraInicio())
                .horaFin(bloque.getHoraFin())
                .disponible(bloque.isDisponible())
                .estado(bloque.getEstado())
                .capacidadMaquina(bloque.getCapacidadMaquina())
                .cuposDisponibles(bloque.getCuposDisponibles())
                .build();
    }
}
