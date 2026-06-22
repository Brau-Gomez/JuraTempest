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
public class ReservaResponseDTO {
    private Long id;
    private Long usuarioId;
    private Long maquinaId;
    private Long horarioId;
    private LocalDate fechaReserva;
    private String estado;
}
