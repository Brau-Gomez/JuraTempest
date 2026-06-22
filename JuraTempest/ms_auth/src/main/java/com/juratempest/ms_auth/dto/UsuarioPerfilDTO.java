package com.juratempest.ms_auth.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPerfilDTO {
    private Long id;
    private Long cuentaId;
    private String nombre;
    private String apellido;
    private String email;
    private Boolean frecuente;
    private Boolean activo;
    private LocalDate fechaRegistro;
}
