package com.juratempest.ms_usuarios_auth.repository;

import com.juratempest.ms_usuarios_auth.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Verifica si ya existe un usuario con el email indicado.
    // Lo usamos antes de registrar para mantener la regla de email unico con una consulta simple.
    boolean existsByEmail(String email);

    // Busca un usuario por email, clave principal funcional para login y consultas externas.
    // Retornar Optional permite expresar que la busqueda puede no encontrar resultados.
    Optional<Usuario> findByEmail(String email);

    // Obtiene usuarios segun la marca de frecuente.
    // Spring Data arma la consulta desde el nombre del metodo sin escribir SQL manual.
    List<Usuario> findByFrecuente(Boolean frecuente);

    // Busca usuarios por el nombre de alguno de sus roles.
    // La expresion RolesNombre navega la relacion ManyToMany entre Usuario y Rol.
    List<Usuario> findByRolesNombre(String nombre);
}
