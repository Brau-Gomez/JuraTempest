package com.juratempest.ms_usuarios.config;

import com.juratempest.ms_usuarios.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    // Define la cadena de filtros de seguridad que Spring Security aplicara a cada request.
    // Lo configuramos stateless porque usamos JWT: el servidor no guarda sesion,
    // cada peticion debe traer su token y el filtro JWT valida la identidad del usuario.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
        throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/internal/users/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/users/*/exists", "/users/email/**").permitAll()
                .requestMatchers("/users/**").hasAnyRole("ADMIN", "OPERADOR")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

}
