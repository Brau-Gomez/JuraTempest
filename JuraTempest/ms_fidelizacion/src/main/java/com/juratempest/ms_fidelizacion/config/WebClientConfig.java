package com.juratempest.ms_fidelizacion.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Expone un WebClient.Builder como bean para inyectarlo en los clientes HTTP del microservicio.
    // @LoadBalanced permite usar el nombre del servicio registrado en Eureka en vez de una URL fija.
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}
