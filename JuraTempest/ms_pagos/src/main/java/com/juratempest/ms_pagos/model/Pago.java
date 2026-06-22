package com.juratempest.ms_pagos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Column(name = "promocion_id")
    private Long promocionId;

    @Column(name = "valor_neto", nullable = false)
    private Integer valorNeto;

    @Column(nullable = false)
    private Integer iva;

    @Column(name = "monto_descuento")
    private Integer montoDescuento;

    @Column(name = "monto_final", nullable = false)
    private Integer montoFinal;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(name = "metodo_pago", nullable = false, length = 30)
    private String metodoPago;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
}
