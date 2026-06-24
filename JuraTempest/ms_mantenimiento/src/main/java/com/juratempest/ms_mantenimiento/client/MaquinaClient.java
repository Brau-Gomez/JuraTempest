package com.juratempest.ms_mantenimiento.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juratempest.ms_mantenimiento.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MaquinaClient {

    private final WebClient.Builder webClientBuilder;

    public MaquinaClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public boolean existe(Long maquinaId) {
        if (maquinaId == null) {
            throw new BadRequestException("La maquina es obligatoria");
        }

        try {
            return Boolean.TRUE.equals(webClientBuilder.build()
                    .get()
                    .uri("http://ms-maquinas/maquinas/{id}/existe", maquinaId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(3))
                    .block());
        } catch (WebClientResponseException.NotFound ex) {
            return false;
        } catch (RuntimeException ex) {
            log.warn("No fue posible validar existencia de maquina maquinaId={}", maquinaId, ex);
            throw new BadRequestException("No fue posible comunicarse con ms_maquinas");
        }
    }

    public boolean estaActiva(Long maquinaId) {
        if (maquinaId == null) {
            throw new BadRequestException("La maquina es obligatoria");
        }

        try {
            return Boolean.TRUE.equals(webClientBuilder.build()
                    .get()
                    .uri("http://ms-maquinas/maquinas/activa/{id}", maquinaId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(3))
                    .block());
        } catch (WebClientResponseException.NotFound ex) {
            return false;
        } catch (RuntimeException ex) {
            log.warn("No fue posible validar estado de maquina maquinaId={}", maquinaId, ex);
            throw new BadRequestException("No fue posible comunicarse con ms_maquinas");
        }
    }
}
