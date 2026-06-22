package com.juratempest.ms_auth.repository;

import com.juratempest.ms_auth.model.Cuenta;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {
    boolean existsByEmail(String email);
    Optional<Cuenta> findByEmail(String email);
}
