package com.juratempest.ms_pagos.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.juratempest.ms_pagos.model.Pago;

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
public class PagoDTO {

    private Long id;

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La reserva es obligatoria")
    private Long reservaId;

    private Long promocionId;

    @NotNull(message = "El valor Neto es obligatorio")
    @Min(value = 1, message = "El valor Neto debe ser mayor a cero")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer valorNeto;

    @NotNull(message = "El iva es obligatorio")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer iva;

    @NotBlank(message = "El metodo de pago es obligatorio")
    private Integer montoDescuento;

    @NotBlank(message = "El metodo de pago es obligatorio")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer montoFinal;

    @NotBlank(message = "El metodo de pago es obligatorio")
    private String metodoPago;

    private String estado;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fechaCreacion;
    

    public Pago toModel() {
        return Pago.builder()
                .id(id)
                .usuarioId(usuarioId)
                .reservaId(reservaId)
                .promocionId(promocionId)
                .valorNeto(valorNeto) 
                .iva(iva != null ? iva : 0)
                .montoDescuento(montoDescuento != null ? montoDescuento : 0)
                .montoFinal(montoFinal != null ? montoFinal : 0)
                .metodoPago(metodoPago)
                .estado(estado)
                .fechaCreacion(fechaCreacion)
                .build();
    }

    public static PagoDTO fromModel(Pago pago) {
        if (pago == null) return null;

        return PagoDTO.builder()
                .id(pago.getId())
                .usuarioId(pago.getUsuarioId())
                .reservaId(pago.getReservaId())
                .promocionId(pago.getPromocionId())
                .valorNeto(pago.getValorNeto())
                .iva(pago.getIva())
                .montoDescuento(pago.getMontoDescuento())
                .montoFinal(pago.getMontoFinal())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .build();
    }
}