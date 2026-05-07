package com.juratempest.ms_bloques.dto;

import java.time.LocalTime;
import jakarta.validation.constraints.NotBlank;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class dtoBloques {
    private Long id;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String diaSemana;
    private boolean disponible;
}
