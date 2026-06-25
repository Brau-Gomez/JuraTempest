package com.juratempest.ms_mantenimiento.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearNotificacionRequestDTO {
    private Long usuarioId;
    private String titulo;
    private String mensaje;
    private String tipo;
    private String canal;
}
