package com.juratempest.ms_fidelizacion.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_fidelizacion.model.Fidelizacion;

public interface FidelizacionRepository extends JpaRepository<Fidelizacion , Long>{
    List<Fidelizacion> findByUsuarioId(Long usuarioId);
}
