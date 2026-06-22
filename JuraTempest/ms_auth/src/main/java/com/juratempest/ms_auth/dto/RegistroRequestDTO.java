package com.juratempest.ms_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @Email(message = "El email debe tener un formato valido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Size(min = 6, message = "La password debe tener al menos 6 caracteres")
    @NotBlank(message = "La password es obligatoria")
    private String password;

    private Boolean frecuente;
    private Set<String> roles;
}
