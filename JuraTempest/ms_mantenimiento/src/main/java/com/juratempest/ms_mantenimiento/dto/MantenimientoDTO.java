package com.juratempest.ms_mantenimiento.dto;

import java.time.LocalDate;

import com.juratempest.ms_mantenimiento.model.Mantenimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MantenimientoDTO {

    private Long id;

    @NotNull(message = "La maquina es obligatoria")
    private Long maquinaId;

    private Long usuarioOperadorId;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotBlank(message = "El tecnico es obligatorio")
    private String tecnico;

    private String estado;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @PositiveOrZero(message = "El costo no puede ser negativo")
    private Integer costo;

    public Mantenimiento toModel() {
        Mantenimiento mantenimiento = new Mantenimiento();
        mantenimiento.setId(id);
        mantenimiento.setMaquinaId(maquinaId);
        mantenimiento.setUsuarioOperadorId(usuarioOperadorId);
        mantenimiento.setTipo(tipo);
        mantenimiento.setDescripcion(descripcion);
        mantenimiento.setTecnico(tecnico);
        mantenimiento.setEstado(estado);
        mantenimiento.setFechaInicio(fechaInicio);
        mantenimiento.setFechaFin(fechaFin);
        mantenimiento.setCosto(costo);
        return mantenimiento;
    }

    public static MantenimientoDTO fromModel(Mantenimiento mantenimiento) {
        if (mantenimiento == null) {
            return null;
        }

        return MantenimientoDTO.builder()
                .id(mantenimiento.getId())
                .maquinaId(mantenimiento.getMaquinaId())
                .usuarioOperadorId(mantenimiento.getUsuarioOperadorId())
                .tipo(mantenimiento.getTipo())
                .descripcion(mantenimiento.getDescripcion())
                .tecnico(mantenimiento.getTecnico())
                .estado(mantenimiento.getEstado())
                .fechaInicio(mantenimiento.getFechaInicio())
                .fechaFin(mantenimiento.getFechaFin())
                .costo(mantenimiento.getCosto())
                .build();
    }
}
