package com.juratempest.ms_fidelizacion.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    // Constructor usado por Spring para inyectar el builder de WebClient.
    // Construimos el cliente aqui para reutilizarlo al consultar otros microservicios.
    public UsuarioClient(WebClient.Builder builder){
        this.webClient = builder.build();
    }

    // Consulta al microservicio de usuarios para validar que el usuario exista antes de asignarle puntos.
    // Usamos timeout y onErrorReturn(false) para que una falla externa no bloquee indefinidamente este servicio.
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
