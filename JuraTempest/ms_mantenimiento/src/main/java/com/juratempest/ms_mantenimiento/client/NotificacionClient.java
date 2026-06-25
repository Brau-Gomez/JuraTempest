package com.juratempest.ms_mantenimiento.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.juratempest.ms_mantenimiento.dto.CrearNotificacionRequestDTO;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificacionClient {

    private final WebClient.Builder webClientBuilder;

    public NotificacionClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public void crearNotificacion(CrearNotificacionRequestDTO request) {
        if (request == null || request.getUsuarioId() == null) {
            return;
        }

        try {
            webClientBuilder.build()
                    .post()
                    .uri("http://ms-notificaciones/notificaciones")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (RuntimeException ex) {
            log.warn("No se pudo crear notificacion de mantenimiento usuarioId={}", request.getUsuarioId());
        }
    }
}
