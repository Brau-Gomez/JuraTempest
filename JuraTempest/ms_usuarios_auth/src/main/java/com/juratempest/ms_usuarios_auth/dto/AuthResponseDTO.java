package com.juratempest.ms_usuarios_auth.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {
    private String token;
    private String tipo;
    private Long usuarioId;
    private String email;
    private Set<String> roles;
}
