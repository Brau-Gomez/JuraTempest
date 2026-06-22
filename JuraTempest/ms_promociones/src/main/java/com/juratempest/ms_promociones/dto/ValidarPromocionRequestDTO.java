package com.juratempest.ms_promociones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidarPromocionRequestDTO {
    private String codigo;
    private Long usuarioId;
    private Long reservaId;
    private Integer montoOriginal;
}
