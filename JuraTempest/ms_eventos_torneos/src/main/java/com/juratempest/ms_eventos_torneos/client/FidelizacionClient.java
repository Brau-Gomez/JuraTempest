package com.juratempest.ms_eventos_torneos.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.juratempest.ms_eventos_torneos.dto.CrearFidelizacionRequestDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FidelizacionClient {

    private final WebClient.Builder webClientBuilder;
    public FidelizacionClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public void registrarPuntos(CrearFidelizacionRequestDTO request) {
        if (request == null) {
            return;
        }

        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://ms-fidelizacion/fidelizacion")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (RuntimeException ex) {
            log.warn("No se pudieron registrar puntos de torneo usuarioId={}", request.getUsuarioId());
        }
    }
}
