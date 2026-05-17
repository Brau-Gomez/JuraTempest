package com.juratempest.ms_reservas.client;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ReservaClient {

    private final WebClient webClient;
    
    // Constructor usado por Spring para inyectar el builder de WebClient.
    // Creamos un cliente reutilizable para consultar otros microservicios antes de guardar reservas.
    public ReservaClient(WebClient.Builder webClient){
        this.webClient = webClient.build();
    }

    // Consulta a ms_usuarios_auth para confirmar que el usuario exista.
    // Si falla la comunicacion, devolvemos false para evitar crear reservas con datos no validados.
    public boolean usuarioExiste (Long id ){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-usuarios-auth/users/{id}/exists", id )
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }

    // Consulta a ms_maquinas para saber si la maquina esta activa.
    // Esta validacion evita reservar maquinas bloqueadas, inactivas o no disponibles.
    public boolean maquinaActiva(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-maquinas/maquinas/activa/{id}", id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }

    // Consulta a ms-horarios para confirmar que el bloque horario exista.
    // La reserva depende de un bloque valido, por eso se valida antes de persistir.
    public boolean bloqueExiste(Long id){
        return Boolean.TRUE.equals(
            webClient.get()
            .uri("http://ms-horarios/horarios/{id}/existe", id)
            .retrieve()
            .bodyToMono(Boolean.class)
            .timeout(Duration.ofSeconds(3))
            .onErrorReturn(false)
            .block()
        );
    }

}
