package com.juratempest.ms_maquinas.dto;

import java.time.LocalDate;

import com.juratempest.ms_maquinas.model.Maquina;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaquinaDTO {

    private Long id;

    @NotNull(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo es obligatorio")
    private String tipo;

    @NotNull(message = "La ubicacion es obligatoria")
    private String ubicacion;

    @NotNull(message = "El estado es obligatorio")
    private String estado;

    @NotNull(message = "El costo por bloque es obligatorio")
    @Min(value = 1, message = "El costo debe ser mayor a 0")
    private Integer costoPorBloque;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fechaInstalacion;

    public Maquina toModel(){
        return new Maquina(id, nombre, tipo, ubicacion, estado, costoPorBloque, fechaInstalacion);
    }

    public static MaquinaDTO fromModel(Maquina m){
        if (m == null) return null;
        return MaquinaDTO.builder()
        .id(m.getId())
        .nombre(m.getNombre())
        .tipo(m.getTipo())
        .ubicacion(m.getUbicacion())
        .estado(m.getEstado().toUpperCase())
        .costoPorBloque(m.getCostoPorBloque())
        .fechaInstalacion(m.getFechaInstalacion())
        .build();
    }
}
