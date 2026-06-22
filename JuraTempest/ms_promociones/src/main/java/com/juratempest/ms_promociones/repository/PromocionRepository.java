package com.juratempest.ms_promociones.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juratempest.ms_promociones.model.Promocion;

public interface PromocionRepository extends JpaRepository<Promocion, Long> {
    Optional<Promocion> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);
    List<Promocion> findByActivaTrue();
    List<Promocion> findByTipo(String tipo);
}
