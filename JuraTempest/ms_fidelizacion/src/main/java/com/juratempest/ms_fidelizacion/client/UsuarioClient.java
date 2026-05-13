package com.juratempest.ms_fidelizacion.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(WebClient.Builder builder){
        this.webClient = builder.build();
    }

    public boolean usuarioExiste(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-usuarios-auth/users/{id}/exists", id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }
}
