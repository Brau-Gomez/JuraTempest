package com.juratempest.ms_auth.service;

import com.juratempest.ms_auth.client.UsuarioAuthClient;
import com.juratempest.ms_auth.dto.AuthResponseDTO;
import com.juratempest.ms_auth.dto.LoginRequestDTO;
import com.juratempest.ms_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_auth.dto.UsuarioPerfilDTO;
import com.juratempest.ms_auth.dto.ValidacionTokenDTO;
import com.juratempest.ms_auth.exception.BadRequestException;
import com.juratempest.ms_auth.model.Cuenta;
import com.juratempest.ms_auth.model.Rol;
import com.juratempest.ms_auth.repository.CuentaRepository;
import com.juratempest.ms_auth.repository.RolRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuthService {
    private final CuentaRepository cuentaRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioAuthClient usuarioAuthClient;
    private final JwtService jwtService;

    public AuthService(
        CuentaRepository cuentaRepository,
        RolRepository rolRepository,
        PasswordEncoder passwordEncoder,
        UsuarioAuthClient usuarioAuthClient,
        JwtService jwtService
    ) {
        this.cuentaRepository = cuentaRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.usuarioAuthClient = usuarioAuthClient;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        Cuenta cuenta = cuentaRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadRequestException("Credenciales invalidas"));

        if (!Boolean.TRUE.equals(cuenta.getActivo())) {
            log.warn("Login rechazado: cuenta inactiva email={}", request.getEmail());
            throw new BadRequestException("Cuenta inactiva");
        }

        if (!passwordEncoder.matches(request.getPassword(), cuenta.getPassword())) {
            log.warn("Login fallido: password incorrecta email={}", request.getEmail());
            throw new BadRequestException("Credenciales invalidas");
        }

        log.info("Login exitoso cuentaId={} email={}", cuenta.getId(), cuenta.getEmail());
        return crearAuthResponse(cuenta);
    }

    @Transactional
    public AuthResponseDTO registrar(RegistroRequestDTO request) {
        if (cuentaRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe una cuenta registrada con ese email");
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setEmail(request.getEmail());
        cuenta.setPassword(passwordEncoder.encode(request.getPassword()));
        cuenta.setActivo(true);
        cuenta.setFechaRegistro(LocalDate.now());
        cuenta.setRoles(obtenerRoles(request.getRoles()));

        Cuenta guardada = cuentaRepository.save(cuenta);
        try {
            UsuarioPerfilDTO perfil = usuarioAuthClient.crearPerfil(guardada.getId(), request);
            guardada.setUsuarioId(perfil.getId());
            Cuenta actualizada = cuentaRepository.save(guardada);
            log.info("Cuenta registrada cuentaId={} usuarioId={} email={}",
                actualizada.getId(), actualizada.getUsuarioId(), actualizada.getEmail());
            return crearAuthResponse(actualizada);
        } catch (RuntimeException ex) {
            cuentaRepository.deleteById(guardada.getId());
            throw ex;
        }
    }

    public ValidacionTokenDTO validarToken(String authorization) {
        String token = extraerToken(authorization);
        boolean valido = jwtService.esTokenValido(token);
        return ValidacionTokenDTO.builder()
            .valido(valido)
            .email(valido ? jwtService.obtenerEmail(token) : null)
            .roles(valido ? jwtService.obtenerRoles(token) : null)
            .build();
    }

    private AuthResponseDTO crearAuthResponse(Cuenta cuenta) {
        Set<String> roles = cuenta.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        return AuthResponseDTO.builder()
            .token(jwtService.generarToken(cuenta))
            .tipo("Bearer")
            .usuarioId(cuenta.getUsuarioId())
            .email(cuenta.getEmail())
            .roles(roles)
            .build();
    }

    private Set<Rol> obtenerRoles(Set<String> nombresRoles) {
        Set<String> rolesSolicitados = nombresRoles == null || nombresRoles.isEmpty()
            ? Set.of("CLIENTE")
            : nombresRoles.stream().map(String::toUpperCase).collect(Collectors.toSet());

        Set<Rol> roles = new HashSet<>();
        for (String nombreRol : rolesSolicitados) {
            Rol rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() -> new BadRequestException("Rol no valido: " + nombreRol));
            roles.add(rol);
        }
        return roles;
    }

    private String extraerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            return "";
        }
        return authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    }
}
