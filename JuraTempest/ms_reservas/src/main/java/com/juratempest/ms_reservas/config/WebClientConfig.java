package com.juratempest.ms_reservas.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    // Expone WebClient.Builder como bean para los clientes HTTP del microservicio.
    // @LoadBalanced permite usar nombres de servicios registrados en Eureka en las URLs.
    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}
