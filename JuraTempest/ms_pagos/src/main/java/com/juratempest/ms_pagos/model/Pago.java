package com.juratempest.ms_pagos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuarioId", nullable = false)
    private Long usuarioId;

    @Column(name = "reservaId", nullable = false)
    private Long reservaId;

    @Column(name = "promocion_id")
    private Long promocionId;

    @NotNull(message = "El valor neto es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int valorNeto;
    
    @NotNull(message = "El valor del IVA es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int iva;

    @Column(name = "monto_descuento")
    private int montoDescuento;

    @Column(name = "monto_final", nullable = false)
    private int montoFinal;

    @Column(nullable = false, length = 30)
    private String estado;
    
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago;


    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
    
}
