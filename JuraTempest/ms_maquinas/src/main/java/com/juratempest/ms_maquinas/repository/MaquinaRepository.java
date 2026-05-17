package com.juratempest.ms_maquinas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_maquinas.model.Maquina;
import java.util.List;


public interface MaquinaRepository extends JpaRepository<Maquina, Long > {
    
    // Busca maquinas por estado, por ejemplo ACTIVA o BLOQUEADA.
    // Spring Data genera la consulta segun el nombre del metodo.
    List<Maquina> findByEstado(String estado);

    // Busca maquinas por tipo para agrupar equipos similares.
    // Evita escribir consultas SQL manuales para filtros simples.
    List<Maquina> findByTipo(String tipo);

    // Busca maquinas por ubicacion fisica.
    // Este metodo permite filtrar equipos por sala, sede o sector.
    List<Maquina> findByUbicacion(String ubicacion);
}
