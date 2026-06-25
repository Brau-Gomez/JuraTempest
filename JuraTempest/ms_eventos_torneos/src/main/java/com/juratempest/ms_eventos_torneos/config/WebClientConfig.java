package com.juratempest.ms_eventos_torneos.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder().filter(propagateAuthorization());
    }

    private ExchangeFilterFunction propagateAuthorization() {
        return (request, next) -> {
            var attributes = RequestContextHolder.getRequestAttributes();
            if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
                return next.exchange(request);
            }
            HttpServletRequest currentRequest = servletAttributes.getRequest();
            String authorization = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null || authorization.isBlank()) {
                return next.exchange(request);
            }
            return next.exchange(ClientRequest.from(request).header(HttpHeaders.AUTHORIZATION, authorization).build());
        };
    }
}
