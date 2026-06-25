package com.juratempest.api_gateway.security;

import java.util.Map;
import java.util.Optional;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (esRutaPublica(request)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);
        Optional<Map<String, Object>> claims = jwtService.obtenerClaimsSiEsValido(token);
        if (claims.isEmpty()) {
            return unauthorized(exchange);
        }

        ServerHttpRequest requestConUsuario = agregarHeadersInternos(request, claims.get());
        return chain.filter(exchange.mutate().request(requestConUsuario).build());
    }

    @Override
    public int getOrder() {
        return -1;
    }

    private boolean esRutaPublica(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        return HttpMethod.OPTIONS.equals(request.getMethod())
                || path.equals("/auth/login")
                || path.equals("/auth/register")
                || path.equals("/auth/validate");
                
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private ServerHttpRequest agregarHeadersInternos(ServerHttpRequest request, Map<String, Object> claims) {
        ServerHttpRequest.Builder builder = request.mutate();
        Object email = claims.get("sub");
        Object usuarioId = claims.get("usuarioId");
        Object roles = claims.get("roles");

        if (email != null) {
            builder.header("X-User-Email", String.valueOf(email));
        }
        if (usuarioId != null) {
            builder.header("X-User-Id", String.valueOf(usuarioId));
        }
        if (roles != null) {
            builder.header("X-User-Roles", String.valueOf(roles));
        }

        return builder.build();
    }
}
