package com.juratempest.ms_reservas.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReservaClient {

    private final WebClient webClient;

    public ReservaClient(WebClient.Builder builder){
        this.webClient = builder.build();
    }

    public boolean usuarioExiste(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
                .uri("http://localhost:9091/users/" + id + "/exists")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block()
        );
    }

    public boolean maquinaActiva(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
                .uri("http://localhost:9092/maquinas/" + id + "/activa")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block()
        );
    }

    public boolean bloqueExiste(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
                .uri("http://localhost:9093/horarios/" + id + "/existe")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block()
        );
    }
}