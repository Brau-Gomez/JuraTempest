package com.juratempest.ms_pagos.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juratempest.ms_pagos.dto.ReservaResponseDTO;
import com.juratempest.ms_pagos.exception.BadRequestException;
import com.juratempest.ms_pagos.exception.ResourceNotFoundException;

@Component
public class ReservaClient {

    private final WebClient.Builder webClientBuilder;

    public ReservaClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public ReservaResponseDTO buscarPorId(Long reservaId) {
        if (reservaId == null) {
            throw new BadRequestException("La reserva es obligatoria");
        }

        try {
            return webClientBuilder.build()
                    .get()
                    .uri("http://ms-reservas/reservas/{id}", reservaId)
                    .retrieve()
                    .bodyToMono(ReservaResponseDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound ex) {
            throw new ResourceNotFoundException("Reserva no encontrada con id " + reservaId);
        } catch (WebClientResponseException ex) {
            throw new BadRequestException("No fue posible validar la reserva: " + ex.getStatusCode());
        } catch (RuntimeException ex) {
            throw new BadRequestException("No fue posible comunicarse con ms_reservas");
        }
    }
}
