package com.juratempest.ms_maquinas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_maquinas.model.Maquina;
import java.util.List;


public interface MaquinaRepository extends JpaRepository<Maquina, Long > {
    
    List<Maquina> findByEstado(String estado);

    List<Maquina> findByTipo(String tipo);

    List<Maquina> findByUbicacion(String ubicacion);
}
