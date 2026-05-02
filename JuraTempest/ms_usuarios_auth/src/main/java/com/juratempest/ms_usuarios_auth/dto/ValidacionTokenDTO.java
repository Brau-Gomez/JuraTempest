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
public class ValidacionTokenDTO {
    private Boolean valido;
    private String email;
    private Set<String> roles;
}
