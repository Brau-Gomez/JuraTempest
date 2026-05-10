package com.juratempest.ms_reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_reservas.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva , Long>{
    List<Reserva> findByUsuarioId(Long usuarioId);
    boolean existsByMaquinaIdAndHorarioId(Long maquinaId, Long horarioId);
}
