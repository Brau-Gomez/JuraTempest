package com.juratempest.ms_reservas.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Long maquinaId;

    @Column(nullable = false)
    private Long horarioId;

    @Column(nullable = false)
    private LocalDate fechaReserva;

    @Column(nullable = false)
    private String estado;
}
