package com.juratempest.ms_horarios.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juratempest.ms_horarios.model.BloqueHorario;

@Repository
public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long>{


    // Busca bloques por una fecha exacta.
    // Spring Data JPA genera la consulta usando el nombre del metodo.
    List<BloqueHorario> findByFecha(LocalDate fecha);

    // Busca solo bloques marcados como disponibles.
    // El sufijo True permite crear la condicion booleana sin escribir SQL.
    List<BloqueHorario> findByDisponibleTrue();

    // Busca bloques por estado.
    // Sirve para filtrar estados como DISPONIBLE, OCUPADO o BLOQUEADO segun la regla del proyecto.
    List<BloqueHorario> findByEstado(String estado);

    // Busca bloques entre dos fechas.
    // Se usa para filtros de calendario y tambien para validar solapamientos en una fecha.
    List<BloqueHorario> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    // Busca bloques disponibles para una fecha especifica.
    // Combina fecha y disponibilidad para consultas mas precisas desde la API.
    List<BloqueHorario> findByFechaAndDisponibleTrue(LocalDate fecha);

}
