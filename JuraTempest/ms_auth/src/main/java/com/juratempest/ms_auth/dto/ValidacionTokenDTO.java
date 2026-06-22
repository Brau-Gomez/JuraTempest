package com.juratempest.ms_auth.dto;

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
    private boolean valido;
    private String email;
    private Set<String> roles;
}
