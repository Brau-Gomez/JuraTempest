package com.juratempest.ms_pagos.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juratempest.ms_pagos.dto.MaquinaResponseDTO;
import com.juratempest.ms_pagos.exception.BadRequestException;
import com.juratempest.ms_pagos.exception.ResourceNotFoundException;

@Component
public class MaquinaClient {

    private final WebClient.Builder webClientBuilder;

    public MaquinaClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public MaquinaResponseDTO buscarPorId(Long maquinaId) {
        if (maquinaId == null) {
            throw new BadRequestException("La maquina es obligatoria");
        }

        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-maquinas/maquinas/{id}", maquinaId)
                    .retrieve()
                    .bodyToMono(MaquinaResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Maquina no encontrada con id " + maquinaId);
        } catch (WebClientResponseException ex) {
            throw new BadRequestException("No fue posible validar la maquina: " + ex.getStatusCode());
        } catch (RuntimeException ex) {
            throw new BadRequestException("No fue posible comunicarse con ms_maquinas");
        }
    }
}
