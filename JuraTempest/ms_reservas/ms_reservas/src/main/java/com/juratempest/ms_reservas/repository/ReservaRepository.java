package com.juratempest.ms_reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_reservas.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva,Long>{
    List<Reserva> findByusuarioId(Long usuarioId);

    List<Reserva> findByEstado(String estado);

    boolean existsByMaquinaIdAndHorarioId(Long maquinaId, Long horarioId);
}
