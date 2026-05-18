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

    // Constructor usado por Spring para inyectar repositorios, codificador de password y servicio JWT.
    // Con inyeccion por constructor la clase declara de forma clara todas sus dependencias obligatorias.
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

    // Registra un usuario nuevo desde el flujo publico y devuelve un token de autenticacion.
    // Validamos email unico, ciframos password y asignamos roles antes de guardar para mantener datos consistentes.
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

    // Autentica al usuario comparando el password recibido contra el hash guardado.
    // Usamos PasswordEncoder.matches porque nunca debemos descifrar ni comparar passwords en texto plano.
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

    // Obtiene todos los usuarios y los convierte a DTO.
    // La conversion evita exponer la entidad completa y protege campos sensibles como password.
    public List<UsuarioDTO> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioDTO::fromModel).toList();
    }

    // Busca un usuario por id y lo devuelve como DTO.
    // Reutiliza obtenerUsuario para mantener en un solo lugar la regla de lanzar 404 si no existe.
    public UsuarioDTO buscarPorId(Long id) {
        return UsuarioDTO.fromModel(obtenerUsuario(id));
    }

    // Verifica existencia por id sin cargar toda la entidad.
    // Es mas eficiente para validaciones entre microservicios donde solo importa saber si el usuario existe.
    public boolean existePorId(Long id) {
        return usuarioRepository.existsById(id);
    }

    // Busca un usuario por email y devuelve un DTO.
    // Si no existe, lanzamos ResourceNotFoundException para que el handler global responda 404.
    public UsuarioDTO buscarPorEmail(String email) {
        return UsuarioDTO.fromModel(usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email " + email)));
    }

    // Lista usuarios asociados a un rol especifico.
    // Normalizamos a mayusculas porque los roles se guardan como valores controlados tipo ADMIN, CLIENTE u OPERADOR.
    public List<UsuarioDTO> buscarPorRol(String rol) {
        return usuarioRepository.findByRolesNombre(rol.toUpperCase()).stream().map(UsuarioDTO::fromModel).toList();
    }

    // Lista usuarios marcados como frecuentes.
    // Esta consulta separa una necesidad habitual de negocio sin cargar usuarios que no cumplen el criterio.
    public List<UsuarioDTO> listarFrecuentes() {
        return usuarioRepository.findByFrecuente(true).stream().map(UsuarioDTO::fromModel).toList();
    }

    // Cuenta la cantidad total de usuarios registrados.
    // Delegamos en JpaRepository.count para que la base de datos haga el conteo de forma eficiente.
    public long totalUsuarios() {
        return usuarioRepository.count();
    }

    // Crea un usuario desde administracion sin iniciar sesion automaticamente.
    // Reutiliza la logica privada de registro y devuelve UsuarioDTO porque no se necesita token.
    public UsuarioDTO crearDesdeAdmin(RegistroRequestDTO request) {
        return UsuarioDTO.fromModel(registrarUsuarioSinToken(request));
    }

    // Actualiza campos editables de un usuario existente.
    // No modificamos email ni password aqui para evitar cambios sensibles mezclados con una edicion general.
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

    // Elimina un usuario por id luego de confirmar que existe.
    // Validar antes permite responder con 404 claro en vez de fallar silenciosamente.
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            log.warn("Intento de eliminar usuario inexistente id={}", id);
            throw new ResourceNotFoundException("Usuario no encontrado con id " + id);
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado id={}", id);
    }

    // Registra un usuario sin emitir token, usado para creacion administrativa.
    // Se mantiene privado porque es una variante interna del registro publico y no deberia exponerse directamente.
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

    // Recupera la entidad Usuario por id o lanza una excepcion de negocio si no existe.
    // Este metodo privado evita repetir el mismo findById y mantiene mensajes de error consistentes.
    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    // Convierte nombres de roles recibidos desde un DTO en entidades Rol existentes.
    // Si no llegan roles asignamos CLIENTE por defecto, y validamos contra la base para evitar roles inventados.
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

    // Construye la respuesta de autenticacion con token, tipo Bearer y datos basicos del usuario.
    // Separar este armado evita duplicar codigo entre registro y login.
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
