package com.juratempest.ms_fidelizacion.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_fidelizacion.dto.FidelizacionDTO;
import com.juratempest.ms_fidelizacion.exception.ResourceNotFoundException;
import com.juratempest.ms_fidelizacion.model.Fidelizacion;
import com.juratempest.ms_fidelizacion.repository.FidelizacionRepository;

@Service
public class FidelizacionService {

    private final FidelizacionRepository repository;

    public FidelizacionService(FidelizacionRepository repository) {
        this.repository = repository;
    }

    // LISTAR
    public List<FidelizacionDTO> listar() {

        return repository.findAll()
                .stream()
                .map(FidelizacionDTO::fromModel)
                .toList();
    }

    // BUSCAR POR ID
    public FidelizacionDTO buscarPorId(Long id) {

        Fidelizacion fidelizacion = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Registro no encontrado"));

        return FidelizacionDTO.fromModel(fidelizacion);
    }

    // BUSCAR POR USUARIO
    public List<FidelizacionDTO> buscarPorUsuario(Long usuarioId) {

        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(FidelizacionDTO::fromModel)
                .toList();
    }

    // CREAR
    public FidelizacionDTO crear(FidelizacionDTO dto) {

        Fidelizacion fidelizacion = dto.toModel();

        fidelizacion.setFechaRegistro(LocalDateTime.now());

        return FidelizacionDTO.fromModel(
                repository.save(fidelizacion)
        );
    }

    // ELIMINAR
    public void eliminar(Long id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Registro no encontrado");
        }

        repository.deleteById(id);
    }

    // TOTAL PUNTOS
    public Long totalPuntos(Long usuarioId) {

        return repository.findByUsuarioId(usuarioId)
                .stream()
                .mapToLong(Fidelizacion::getPuntos)
                .sum();
    }
}