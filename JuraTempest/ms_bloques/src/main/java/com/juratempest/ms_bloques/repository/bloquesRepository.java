package com.juratempest.ms_bloques.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juratempest.ms_bloques.model.Bloques;

@Repository
public interface bloquesRepository extends JpaRepository<Bloques, Long> {
    List<Bloques> findByDisponibleTrue();

}
