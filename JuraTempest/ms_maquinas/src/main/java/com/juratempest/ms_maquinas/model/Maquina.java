package com.juratempest.ms_maquinas.model;

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
//probaandoooo
//probando
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "maquina")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String ubicacion;

    @Column(nullable = false, length = 30)
    private String estado;

    @Column(nullable = false)
    private Integer costoPorBloque;

    @Column(nullable = false)
    private LocalDate fechaInstalacion;

    
}
