package com.juratempest.ms_notificaciones.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class UsuarioClient {

    private final WebClient webClientBuilder;

    public UsuarioClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder.build();
    }

    public boolean usuarioExiste(Long usuarioId) {
        return Boolean.TRUE.equals(
            webClientBuilder.get()
            .uri("http://ms-usuarios/users/{id}/exists", usuarioId)
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .doOnError(ex -> log.warn("ERROR consultando existencia de usuario usuarioId={}", usuarioId, ex))
            .onErrorReturn(false)
            .block()
        );
    }
}
