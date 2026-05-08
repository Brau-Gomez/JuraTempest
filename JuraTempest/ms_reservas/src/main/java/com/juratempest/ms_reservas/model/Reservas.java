package com.juratempest.ms_reservas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"maquinaId", "horarioId"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reservas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long usuarioId;
    private Long maquinaId;
    private Long horarioId;

    private String estado;

    private LocalDateTime fechaReserva;
}
