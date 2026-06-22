package com.juratempest.ms_promociones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidarPromocionResponseDTO {
    private Boolean valida;
    private String mensaje;
    private Integer porcentajeDescuento;
    private Integer montoDescuento;
    private Integer montoFinal;
    private Long promocionId;
}
