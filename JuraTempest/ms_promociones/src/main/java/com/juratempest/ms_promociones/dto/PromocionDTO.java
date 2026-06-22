package com.juratempest.ms_promociones.dto;

import java.time.LocalDate;

import com.juratempest.ms_promociones.model.Promocion;

import jakarta.validation.constraints.Max;
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
public class PromocionDTO {

    private Long id;

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La descripcion es obligatoria")
    private String descripcion;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @Min(value = 1, message = "El descuento debe ser minimo 1")
    @Max(value = 100, message = "El descuento no puede superar 100")
    private Integer porcentajeDescuento;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    private Boolean activa;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    public Promocion toModel() {
        return Promocion.builder()
                .id(id)
                .codigo(codigo)
                .nombre(nombre)
                .descripcion(descripcion)
                .porcentajeDescuento(porcentajeDescuento)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .activa(activa)
                .tipo(tipo)
                .build();
    }

    public static PromocionDTO fromModel(Promocion promocion) {
        if (promocion == null) {
            return null;
        }

        return PromocionDTO.builder()
                .id(promocion.getId())
                .codigo(promocion.getCodigo())
                .nombre(promocion.getNombre())
                .descripcion(promocion.getDescripcion())
                .porcentajeDescuento(promocion.getPorcentajeDescuento())
                .fechaInicio(promocion.getFechaInicio())
                .fechaFin(promocion.getFechaFin())
                .activa(promocion.getActiva())
                .tipo(promocion.getTipo())
                .build();
    }
}
