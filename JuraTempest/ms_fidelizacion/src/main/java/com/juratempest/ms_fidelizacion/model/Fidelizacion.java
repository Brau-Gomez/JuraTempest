package com.juratempest.ms_fidelizacion.model;

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
@Table(name = "fidelizacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fidelizacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long usuarioId;
    @Column(nullable = false)
    private Integer puntos;
    @Column(nullable = false)
    private String descripcion;
    @Column(nullable = false)
    private LocalDate fechaRegistro;
}
