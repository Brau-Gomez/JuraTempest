package com.juratempest.ms_usuarios_auth.repository;

import com.juratempest.ms_usuarios_auth.model.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Busca un rol por su nombre usando query method de Spring Data JPA.
    // Retornamos Optional para obligar a manejar el caso en que el rol solicitado no exista.
    Optional<Rol> findByNombre(String nombre);
}
