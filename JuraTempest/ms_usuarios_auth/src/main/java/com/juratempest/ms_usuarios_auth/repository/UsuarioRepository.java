package com.juratempest.ms_usuarios_auth.repository;

import com.juratempest.ms_usuarios_auth.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByFrecuente(Boolean frecuente);

    List<Usuario> findByRolesNombre(String nombre);
}
