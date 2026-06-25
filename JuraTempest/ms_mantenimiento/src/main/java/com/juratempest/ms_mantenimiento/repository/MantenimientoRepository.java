package com.juratempest.ms_mantenimiento.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_mantenimiento.model.Mantenimiento;

public interface MantenimientoRepository extends JpaRepository<Mantenimiento, Long> {
    List<Mantenimiento> findByMaquinaId(Long maquinaId);
    List<Mantenimiento> findByEstado(String estado);
    List<Mantenimiento> findByTipo(String tipo);
}
