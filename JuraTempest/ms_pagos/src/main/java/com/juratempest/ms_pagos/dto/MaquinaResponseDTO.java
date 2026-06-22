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
public class MaquinaResponseDTO {
    private Long id;
    private String nombre;
    private String tipo;
    private String ubicacion;
    private String estado;
    private Integer costoPorBloque;
    private LocalDate fechaInstalacion;
}
