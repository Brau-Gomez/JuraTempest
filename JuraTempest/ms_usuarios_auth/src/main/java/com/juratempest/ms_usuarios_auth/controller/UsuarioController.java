package com.juratempest.ms_usuarios_auth.controller;

import com.juratempest.ms_usuarios_auth.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios_auth.dto.UsuarioDTO;
import com.juratempest.ms_usuarios_auth.service.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsuarioController {
    private final UsuarioService usuarioService;

    // Constructor usado por Spring para entregar el servicio de usuarios al controlador.
    // La inyeccion por constructor facilita pruebas y evita dependencias ocultas.
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Lista todos los usuarios transformados a DTO para no exponer directamente la entidad JPA.
    // Respondemos con ResponseEntity para controlar de forma explicita la respuesta HTTP.
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    // Busca un usuario especifico por id usando una variable de ruta.
    // Dejamos la busqueda y el manejo de "no encontrado" al servicio para centralizar la regla.
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // Informa si existe un usuario por id sin devolver todos sus datos.
    // Este endpoint es util para que otros microservicios validen referencias de usuario de forma liviana.
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existePorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.existePorId(id));
    }

    // Busca un usuario por email y lo entrega como DTO.
    // Usamos PathVariable porque el email forma parte directa del recurso consultado.
    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    // Obtiene usuarios que tengan un rol determinado.
    // Delegamos al servicio para normalizar el nombre del rol y reutilizar la consulta del repositorio.
    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioDTO>> buscarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.buscarPorRol(rol));
    }

    // Lista usuarios marcados como frecuentes.
    // Separar este caso en un endpoint ayuda cuando la interfaz necesita cargar clientes recurrentes rapidamente.
    @GetMapping("/frecuentes")
    public ResponseEntity<List<UsuarioDTO>> listarFrecuentes() {
        return ResponseEntity.ok(usuarioService.listarFrecuentes());
    }

    // Devuelve la cantidad total de usuarios en una estructura JSON simple.
    // Usamos Map para responder {"total": valor} en vez de devolver un numero suelto.
    @GetMapping("/total")
    public ResponseEntity<Map<String, Long>> totalUsuarios() {
        return ResponseEntity.ok(Map.of("total", usuarioService.totalUsuarios()));
    }

    // Crea un usuario desde una accion administrativa.
    // Recibe el mismo DTO de registro, pero devuelve UsuarioDTO porque aqui no corresponde emitir token de login.
    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody RegistroRequestDTO request) {
        return ResponseEntity.status(201).body(usuarioService.crearDesdeAdmin(request));
    }

    // Actualiza datos editables de un usuario existente.
    // El id viene por URL para identificar el recurso, y el cuerpo trae los valores nuevos.
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizar(id, dto));
    }

    // Elimina un usuario por id y entrega un mensaje simple de confirmacion.
    // La validacion de existencia queda en el servicio para mantener consistente la regla de negocio.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok("Se ha eliminado correctamente");
    }
}
