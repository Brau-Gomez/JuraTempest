package com.juratempest.ms_usuarios.security;

import com.juratempest.ms_usuarios.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    // Constructor usado por Spring para inyectar el servicio encargado de validar y leer JWT.
    // El filtro depende de JwtService para no mezclar criptografia y reglas HTTP en la misma clase.
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // Se ejecuta una vez por cada request y revisa si viene un token Bearer valido.
    // Si el token es correcto, cargamos la autenticacion en SecurityContextHolder para que Spring Security
    // pueda aplicar reglas como hasAnyRole en la configuracion de seguridad.
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtService.esTokenValido(token)) {
                List<SimpleGrantedAuthority> authorities = jwtService.obtenerRoles(token).stream()
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol))
                    .toList();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    jwtService.obtenerEmail(token),
                    null,
                    authorities
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
