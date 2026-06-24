package com.juratempest.ms_mantenimiento.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juratempest.ms_mantenimiento.exception.BadRequestException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient.Builder webClientBuilder;

    public UsuarioClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public boolean usuarioExiste(Long usuarioId) {
        if (usuarioId == null) {
            return true;
        }

        try {
            return Boolean.TRUE.equals(webClientBuilder.build()
                    .get()
                    .uri("http://ms-usuarios/users/{id}/exists", usuarioId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .timeout(Duration.ofSeconds(3))
                    .block());
        } catch (WebClientResponseException.NotFound ex) {
            return false;
        } catch (RuntimeException ex) {
            log.warn("No fue posible validar usuario usuarioId={}", usuarioId, ex);
            throw new BadRequestException("No fue posible comunicarse con ms_usuarios");
        }
    }
}
