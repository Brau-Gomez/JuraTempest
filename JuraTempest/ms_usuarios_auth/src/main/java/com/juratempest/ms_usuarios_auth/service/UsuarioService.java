package com.juratempest.ms_usuarios_auth.service;

import com.juratempest.ms_usuarios_auth.dto.AuthResponseDTO;
import com.juratempest.ms_usuarios_auth.dto.LoginRequestDTO;
import com.juratempest.ms_usuarios_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios_auth.dto.UsuarioDTO;
import com.juratempest.ms_usuarios_auth.exception.BadRequestException;
import com.juratempest.ms_usuarios_auth.exception.ResourceNotFoundException;
import com.juratempest.ms_usuarios_auth.model.Rol;
import com.juratempest.ms_usuarios_auth.model.Usuario;
import com.juratempest.ms_usuarios_auth.repository.RolRepository;
import com.juratempest.ms_usuarios_auth.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        RolRepository rolRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDTO registrar(RegistroRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            log.warn("Email ya existe, intente con otro email {}", request.getEmail());
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setFrecuente(Boolean.TRUE.equals(request.getFrecuente()));
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setRoles(obtenerRoles(request.getRoles()));

        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario registrado id={} email={}", guardado.getId(), guardado.getEmail());
        return crearAuthResponse(guardado);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            log.warn("Login rechazado: usuario inactivo email={}", request.getEmail());
            throw new BadCredentialsException("Usuario inactivo");
        }

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            log.warn("Login fallido: password incorrecta email={}", request.getEmail());
            throw new BadCredentialsException("Credenciales invalidas");
        }

        log.info("Login exitoso email={}", request.getEmail());
        return crearAuthResponse(usuario);
    }

    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioDTO::fromModel).toList();
    }

    public UsuarioDTO buscarPorId(Long id) {
        return UsuarioDTO.fromModel(obtenerUsuario(id));
    }

    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    public UsuarioDTO buscarPorEmail(String email) {
        return UsuarioDTO.fromModel(usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email " + email)));
    }

    public List<UsuarioDTO> buscarPorRol(String rol) {
        return usuarioRepository.findByRolesNombre(rol.toUpperCase()).stream().map(UsuarioDTO::fromModel).toList();
    }

    public List<UsuarioDTO> listarFrecuentes() {
        return usuarioRepository.findByFrecuente(true).stream().map(UsuarioDTO::fromModel).toList();
    }

    public long totalUsuarios() {
        return usuarioRepository.count();
    }

    public UsuarioDTO crearDesdeAdmin(RegistroRequestDTO request) {
        return UsuarioDTO.fromModel(registrarUsuarioSinToken(request));
    }

    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setFrecuente(Boolean.TRUE.equals(dto.getFrecuente()));
        usuario.setActivo(dto.getActivo() == null || dto.getActivo());
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            usuario.setRoles(obtenerRoles(dto.getRoles()));
        }
        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario actualizado id={}", id);
        return UsuarioDTO.fromModel(actualizado);
    }

    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id " + id);
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado id={}", id);
    }

    private Usuario registrarUsuarioSinToken(RegistroRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellido(request.getApellido());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setFrecuente(Boolean.TRUE.equals(request.getFrecuente()));
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDate.now());
        usuario.setRoles(obtenerRoles(request.getRoles()));
        Usuario guardado = usuarioRepository.save(usuario);
        log.info("Usuario creado por administracion id={} email={}", guardado.getId(), guardado.getEmail());
        return guardado;
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
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

    private AuthResponseDTO crearAuthResponse(Usuario usuario) {
        Set<String> roles = usuario.getRoles().stream().map(Rol::getNombre).collect(Collectors.toSet());
        return AuthResponseDTO.builder()
            .token(jwtService.generarToken(usuario))
            .tipo("Bearer")
            .usuarioId(usuario.getId())
            .email(usuario.getEmail())
            .roles(roles)
            .build();
    }
}
