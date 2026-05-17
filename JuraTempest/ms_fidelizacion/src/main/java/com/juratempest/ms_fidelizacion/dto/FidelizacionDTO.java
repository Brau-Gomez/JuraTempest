package com.juratempest.ms_fidelizacion.dto;

import java.time.LocalDate;
import com.juratempest.ms_fidelizacion.model.Fidelizacion;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FidelizacionDTO {
    private Long id;
    
    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "Los puntos son obligatorios")
    @Min(value = 1, message = "Los puntos deben ser mayores a 0")
    private Integer puntos;

    @Size(max = 200, message = "La descripcion no puede superar los 200 caracteres")
    private String descripcion;

    private LocalDate fechaRegistro;

    // Convierte el DTO a entidad para guardar o actualizar datos con JPA.
    // Esta separacion permite que la API trabaje con DTO sin exponer directamente la entidad.
    public Fidelizacion toModel(){
        return new Fidelizacion(id, usuarioId, puntos, descripcion, fechaRegistro);
    }

    // Convierte una entidad de base de datos a DTO para responder al cliente.
    // Usamos builder para construir la respuesta de forma legible y controlar que campos salen por la API.
    public static FidelizacionDTO fromModel(Fidelizacion f){
        if (f == null) return null;

        return FidelizacionDTO.builder()
        .id(f.getId())
        .usuarioId(f.getUsuarioId())
        .puntos(f.getPuntos())
        .descripcion(f.getDescripcion())
        .fechaRegistro(f.getFechaRegistro())
        .build();
    }

}
