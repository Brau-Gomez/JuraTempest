package com.juratempest.ms_eventos_torneos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_eventos_torneos.model.Torneo;

public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    List<Torneo> findByEstado(String estado);
    List<Torneo> findByEstadoAndCuposDisponiblesGreaterThan(String estado, Integer cuposDisponibles);
}
