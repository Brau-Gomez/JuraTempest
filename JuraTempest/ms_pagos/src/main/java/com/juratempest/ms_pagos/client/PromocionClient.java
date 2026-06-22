package com.juratempest.ms_pagos.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juratempest.ms_pagos.dto.PromocionResponseDTO;
import com.juratempest.ms_pagos.exception.BadRequestException;
import com.juratempest.ms_pagos.exception.ResourceNotFoundException;

@Component
public class PromocionClient {

    private final WebClient.Builder webClientBuilder;

    public PromocionClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public PromocionResponseDTO buscarPorId(Long promocionId) {
        if (promocionId == null) {
            return null;
        }

        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-promociones/promociones/{id}", promocionId)
                    .retrieve()
                    .bodyToMono(PromocionResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Promocion no encontrada con id " + promocionId);
        } catch (WebClientResponseException ex) {
            throw new BadRequestException("No fue posible validar la promocion: " + ex.getStatusCode());
        } catch (RuntimeException ex) {
            throw new BadRequestException("No fue posible comunicarse con ms_promociones");
        }
    }
}
