package com.juratempest.ms_pagos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearFidelizacionRequestDTO {
    private Long usuarioId;
    private Integer puntos;
    private String descripcion;
}
