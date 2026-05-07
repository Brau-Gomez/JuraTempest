package com.juratempest.ms_horarios.model;

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

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bloque_horario")
public class BloqueHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dia;

    @Column(nullable = false)
    private LocalDate horaInicio;

    @Column(nullable = false)
    private LocalDate horaFin;

    @Column(nullable = false)
    private boolean disponible;

    @Column(nullable = false)
    private String estado;
    @Column(nullable = false)
    private String capacidadMaquina;
    @Column(nullable = false)
    private int cuposDisponibles;


    


}
