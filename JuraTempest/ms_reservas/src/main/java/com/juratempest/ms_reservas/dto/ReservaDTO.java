package com.juratempest.ms_reservas.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.juratempest.ms_reservas.model.Reserva;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ReservaDTO {
    private Long id;

    @NotNull(message = "El id de usuario es obligatorio")
    private Long usuarioID;

    @NotNull(message = "El id de la maquina es obligatorio")
    private Long maquinaId;

    @NotNull(message = "El id de horario es obligatorio")
    private Long horarioId;
    
    @NotNull
    private LocalDate fechaReserva;

    private String estado;

    public Reserva toModel(){
        return new Reserva(id,usuarioID,maquinaId,horarioId,fechaReserva,estado);
    }

    public static ReservaDTO fromModel(Reserva r ){
        if(r==null) return null;
        return ReservaDTO.builder()
        .id(r.getId())
        .usuarioID(r.getUsuarioId())
        .maquinaId(r.getMaquinaId())
        .horarioId(r.getHorarioId())
        .fechaReserva(r.getFechaReserva())
        .estado(r.getEstado())
        .build();
        
    }
}
