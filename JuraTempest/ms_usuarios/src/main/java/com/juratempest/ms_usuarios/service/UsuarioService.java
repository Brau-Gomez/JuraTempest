package com.juratempest.ms_usuarios.service;

import com.juratempest.ms_usuarios.dto.CrearPerfilUsuarioRequestDTO;
import com.juratempest.ms_usuarios.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios.dto.UsuarioDTO;
import com.juratempest.ms_usuarios.exception.BadRequestException;
import com.juratempest.ms_usuarios.exception.ResourceNotFoundException;
import com.juratempest.ms_usuarios.model.Usuario;
import com.juratempest.ms_usuarios.repository.UsuarioRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    // Constructor usado por Spring para inyectar el repositorio de perfiles.
    // Con inyeccion por constructor la clase declara de forma clara todas sus dependencias obligatorias.
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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

    // Crea solo el perfil de usuario cuando la cuenta ya fue creada en ms-auth.
    // El password guardado es un placeholder no utilizable; las credenciales reales viven en ms-auth.
    public UsuarioDTO crearPerfilDesdeAuth(CrearPerfilUsuarioRequestDTO request) {
        validarUnico(request.getCuentaId(), request.getEmail());
        Usuario guardado = usuarioRepository.save(crearPerfil(
            request.getCuentaId(),
            request.getNombre(),
            request.getApellido(),
            request.getEmail(),
            request.getFrecuente()
        ));
        log.info("Perfil de usuario creado desde ms-auth id={} email={}", guardado.getId(), guardado.getEmail());
        return UsuarioDTO.fromModel(guardado);
    }

    // Actualiza campos editables de un usuario existente.
    // No modificamos email ni password aqui para evitar cambios sensibles mezclados con una edicion general.
    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario usuario = obtenerUsuario(id);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setFrecuente(Boolean.TRUE.equals(dto.getFrecuente()));
        usuario.setActivo(dto.getActivo() == null || dto.getActivo());
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
        validarUnico(request.getCuentaId(), request.getEmail());
        Usuario guardado = usuarioRepository.save(crearPerfil(
            request.getCuentaId(),
            request.getNombre(),
            request.getApellido(),
            request.getEmail(),
            request.getFrecuente()
        ));
        log.info("Usuario creado por administracion id={} email={}", guardado.getId(), guardado.getEmail());
        return guardado;
    }

    // Recupera la entidad Usuario por id o lanza una excepcion de negocio si no existe.
    // Este metodo privado evita repetir el mismo findById y mantiene mensajes de error consistentes.
    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private void validarUnico(Long cuentaId, String email) {
        if (usuarioRepository.existsByCuentaId(cuentaId)) {
            throw new BadRequestException("Ya existe un perfil asociado a esa cuenta");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new BadRequestException("Ya existe un perfil de usuario con ese email");
        }
    }

    private Usuario crearPerfil(Long cuentaId, String nombre, String apellido, String email, Boolean frecuente) {
        Usuario usuario = new Usuario();
        usuario.setCuentaId(cuentaId);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setFrecuente(Boolean.TRUE.equals(frecuente));
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDate.now());
        return usuario;
    }

}
