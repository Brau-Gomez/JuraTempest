package com.juratempest.ms_auth.client;

import com.juratempest.ms_auth.dto.CrearPerfilUsuarioRequestDTO;
import com.juratempest.ms_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_auth.dto.UsuarioPerfilDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UsuarioAuthClient {
    private final WebClient webClient;

    public UsuarioAuthClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ms-usuarios").build();
    }

    public UsuarioPerfilDTO crearPerfil(Long cuentaId, RegistroRequestDTO request) {
        CrearPerfilUsuarioRequestDTO perfil = CrearPerfilUsuarioRequestDTO.builder()
            .cuentaId(cuentaId)
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .email(request.getEmail())
            .frecuente(request.getFrecuente())
            .build();
        return webClient.post()
            .uri("/internal/users/profiles")
            .bodyValue(perfil)
            .retrieve()
            .bodyToMono(UsuarioPerfilDTO.class)
            .block();
    }
}
