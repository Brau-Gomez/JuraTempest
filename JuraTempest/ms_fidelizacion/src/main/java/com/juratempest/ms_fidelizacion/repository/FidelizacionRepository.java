package com.juratempest.ms_fidelizacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_fidelizacion.model.Fidelizacion;

public interface FidelizacionRepository extends JpaRepository<Fidelizacion , Long>{
    // Busca todos los registros de puntos asociados a un usuario.
    // Spring Data JPA genera la consulta automaticamente a partir del nombre del metodo.
    List<Fidelizacion> findByUsuarioId(Long usuarioId);
}
