package com.juratempest.ms_pagos.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromocionResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Integer porcentajeDescuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activa;
    private String tipo;
}
