package com.juratempest.ms_reservas.dto;

import java.time.LocalDate;

import com.juratempest.ms_reservas.model.Reserva;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    private Long usuarioId;

    @NotNull(message = "El id de la maquina es obligatorio")
    private Long maquinaId;

    @NotNull(message = "El id de horario es obligatorio")
    private Long horarioId;

    private LocalDate fechaReserva;
    
    @Pattern(regexp = "ACTIVA|CANCELADA|FINALIZADA", message = "El estado debe ser ACTIVA, CANCELADA o FINALIZADA")
    private String estado;

    // Convierte el DTO a entidad Reserva para persistirla con JPA.
    // Usamos DTO para validar y transportar datos sin exponer directamente el modelo de base de datos.
    public Reserva toModel(){
        return new Reserva(id,usuarioId,maquinaId,horarioId,fechaReserva,estado);
    }

    // Convierte una entidad Reserva a DTO para responder al cliente.
    // Este metodo centraliza el mapeo y evita repetir armado de respuestas en controllers o services.
    public static ReservaDTO fromModel(Reserva r ){
        if(r==null) return null;
        return ReservaDTO.builder()
        .id(r.getId())
        .usuarioId(r.getUsuarioId())
        .maquinaId(r.getMaquinaId())
        .horarioId(r.getHorarioId())
        .fechaReserva(r.getFechaReserva())
        .estado(r.getEstado())
        .build();
        
    }
}
