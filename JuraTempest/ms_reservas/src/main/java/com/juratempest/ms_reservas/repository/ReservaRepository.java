package com.juratempest.ms_reservas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_reservas.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva,Long>{
    // Busca reservas de un usuario especifico.
    // Spring Data genera la consulta a partir del nombre del metodo.
    List<Reserva> findByUsuarioId(Long usuarioId);

    // Busca reservas por estado para filtrar activas, canceladas o finalizadas.
    // Evita filtrar manualmente en memoria despues de traer todos los registros.
    List<Reserva> findByEstado(String estado);

    // Verifica si existe otra reserva para la misma maquina y horario, excluyendo la reserva actual.
    // Se usa al actualizar para evitar falsos positivos contra el mismo registro.
    boolean existsByMaquinaIdAndHorarioIdAndIdNot(Long maquinaId, Long horarioId, Long id);

    // Verifica si ya existe una reserva para una maquina en un bloque horario.
    // Se usa al crear para impedir doble reserva del mismo recurso.
    boolean existsByMaquinaIdAndHorarioId(Long maquinaId, Long horarioId);
}
