package com.juratempest.ms_horarios.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juratempest.ms_horarios.model.BloqueHorario;

@Repository
public interface BloqueHorarioRepository extends JpaRepository<BloqueHorario, Long>{


    List<BloqueHorario> findByFecha(LocalDate fecha);

    List<BloqueHorario> findByDisponibleTrue();

    List<BloqueHorario> findByEstado(String estado);

    List<BloqueHorario> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    List<BloqueHorario> findByFechaAndDisponibleTrue(LocalDate fecha);

}
