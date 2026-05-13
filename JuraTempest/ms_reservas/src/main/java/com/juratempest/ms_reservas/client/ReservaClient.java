package com.juratempest.ms_reservas.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReservaClient {

    private final WebClient webClient;
    
    public ReservaClient(WebClient.Builder webClient){
        this.webClient = webClient.build();
    }

    public boolean usuarioExiste (Long id ){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-usuarios-auth/users/{id}/exists" + id )
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }

    public boolean maquinaActiva(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-maquinas/maquinas/{id}/activa", + id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }

    public boolean bloqueExiste(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-horarios/horarios/{id}/existe", + id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }

}
