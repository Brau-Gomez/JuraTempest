package com.juratempest.ms_bloques.repository;

@Repository
public interface bloquesRepository extends JpaRepository<Bloques, Long> {
    List<Bloques> findByDisponibleTrue();

}
