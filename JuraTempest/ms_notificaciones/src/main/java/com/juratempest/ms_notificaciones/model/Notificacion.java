package com.juratempest.ms_notificaciones.model;

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
@Table(name = "notificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "titulo", nullable = false)
    private String titulo;
    @Column(name = "mensaje", nullable = false)
    private String mensaje;
    @Column(name = "tipo", nullable = false)
    private String tipo;
    @Column(name = "canal", nullable = false)
    private String canal;
    @Column(name = "leida",nullable = false)
    private Boolean leida;
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
