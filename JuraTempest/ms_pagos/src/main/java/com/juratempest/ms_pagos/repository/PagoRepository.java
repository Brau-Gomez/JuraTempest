package com.juratempest.ms_pagos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_pagos.model.Pago;

public interface PagoRepository extends JpaRepository<Pago, Long>{

    List<Pago> findByUsuarioId(Long usuarioId);

    List<Pago> findByReservaId(Long reservaId);

    List<Pago> findByEstado(String estado);

    List<Pago> findByMetodoPago(String metodoPago);

    boolean existsByReservaIdAndEstado(Long reservaId, String estado);

    boolean existsByReservaIdAndEstadoAndIdNot(Long reservaId, String estado, Long id);
}
