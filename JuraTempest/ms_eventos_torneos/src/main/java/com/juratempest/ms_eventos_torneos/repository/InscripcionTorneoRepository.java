package com.juratempest.ms_eventos_torneos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_eventos_torneos.model.InscripcionTorneo;

public interface InscripcionTorneoRepository extends JpaRepository<InscripcionTorneo, Long> {
    List<InscripcionTorneo> findByTorneoId(Long torneoId);
    List<InscripcionTorneo> findByUsuarioId(Long usuarioId);
    List<InscripcionTorneo> findByTorneoIdAndEstado(Long torneoId, String estado);
    Optional<InscripcionTorneo> findByTorneoIdAndUsuarioIdAndEstado(Long torneoId, Long usuarioId, String estado);
    boolean existsByTorneoIdAndEstado(Long torneoId, String estado);
}
