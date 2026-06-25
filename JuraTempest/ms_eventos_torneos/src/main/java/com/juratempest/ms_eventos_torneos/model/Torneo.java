package com.juratempest.ms_eventos_torneos.model;

import java.time.LocalDate;

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
@Table(name = "torneo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "maquina_id", nullable = false)
    private Long maquinaId;

    @Column(name = "horario_id", nullable = false)
    private Long horarioId;

    @Column(name = "cupos_maximos", nullable = false)
    private Integer cuposMaximos;

    @Column(name = "cupos_disponibles", nullable = false)
    private Integer cuposDisponibles;

    @Column(nullable = false)
    private String estado;

    @Column(name = "ganador_usuario_id")
    private Long ganadorUsuarioId;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDate fechaCreacion;
}
