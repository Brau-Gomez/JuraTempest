package com.juratempest.ms_reservas.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long maquinaId;

    @Column(nullable = false)
    private Long bloqueId;

    @Column(nullable = false)
    private LocalDate fechaReserva;

    @Column(nullable = false)
    private String estado;

}