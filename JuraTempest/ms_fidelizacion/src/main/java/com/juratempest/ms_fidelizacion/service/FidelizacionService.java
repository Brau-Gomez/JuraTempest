package com.juratempest.ms_fidelizacion.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_fidelizacion.client.UsuarioClient;
import com.juratempest.ms_fidelizacion.dto.FidelizacionDTO;
import com.juratempest.ms_fidelizacion.exception.ResourceNotFoundException;
import com.juratempest.ms_fidelizacion.model.Fidelizacion;
import com.juratempest.ms_fidelizacion.repository.FidelizacionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FidelizacionService {

    private final FidelizacionRepository repository;
    private final UsuarioClient usuarioClient;

    public FidelizacionService(FidelizacionRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
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
        if (!usuarioClient.usuarioExiste(dto.getUsuarioId())){
            throw new ResourceNotFoundException("Usuario no existe");
        }
        Fidelizacion fidelizacion = dto.toModel();

        fidelizacion.setFechaRegistro(LocalDate.now());
        log.info("Registro creado con exito id={}", fidelizacion.getId());
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
        log.info("Registro eliminado con exito id={}" , id);
    }

    // TOTAL PUNTOS
    public Long totalPuntos(Long usuarioId) {
        
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .mapToLong(Fidelizacion::getPuntos)
                .sum();
    }
    
    //ACTUALIZAR
    public FidelizacionDTO actualizar(Long id, FidelizacionDTO dto){

        if (!usuarioClient.usuarioExiste(dto.getUsuarioId())){
            throw new ResourceNotFoundException("Usuario no existe en la base de datos");
        }
        Fidelizacion fidelizacion = repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Registro no encontrado"));
        
        fidelizacion.setUsuarioId(dto.getUsuarioId());
        fidelizacion.setPuntos(dto.getPuntos());
        fidelizacion.setDescripcion(dto.getDescripcion());
        fidelizacion.setFechaRegistro(dto.getFechaRegistro());

        Fidelizacion actualizada = repository.save(fidelizacion);
        log.info("Registro actualizado id={}", id);

        return FidelizacionDTO.fromModel(actualizada);
    }
}