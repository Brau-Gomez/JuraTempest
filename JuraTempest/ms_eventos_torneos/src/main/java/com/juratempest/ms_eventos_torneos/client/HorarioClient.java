package com.juratempest.ms_eventos_torneos.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juratempest.ms_eventos_torneos.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HorarioClient {

    private final WebClient.Builder webClientBuilder;
    public HorarioClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public boolean existe(Long horarioId) {
        if (horarioId == null) {
            throw new BadRequestException("El horario es obligatorio");
        }

        try {
            return Boolean.TRUE.equals(webClientBuilder.build()
                    .get()
                    .uri("http://ms-horarios/horarios/{id}/existe", horarioId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(3))
                    .block());
        } catch (WebClientResponseException.NotFound ex) {
            return false;
        } catch (RuntimeException ex) {
            log.warn("No fue posible validar horario horarioId={}", horarioId, ex);
            throw new BadRequestException("No fue posible comunicarse con ms_horarios");
        }
    }
}
