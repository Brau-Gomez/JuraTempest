package com.juratempest.ms_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearPerfilUsuarioRequestDTO {
    private Long cuentaId;
    private String nombre;
    private String apellido;
    private String email;
    private Boolean frecuente;
}
