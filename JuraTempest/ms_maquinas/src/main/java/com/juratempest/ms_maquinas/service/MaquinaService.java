package com.juratempest.ms_maquinas.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.juratempest.ms_maquinas.dto.MaquinaDTO;
import com.juratempest.ms_maquinas.exception.ResourceNotFoundException;
import com.juratempest.ms_maquinas.model.Maquina;
import com.juratempest.ms_maquinas.repository.MaquinaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaquinaService {
    private final MaquinaRepository maquinaRepository;

    public MaquinaService(MaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    public List<MaquinaDTO> listar(){
        return maquinaRepository.findAll()
        .stream()
        .map(MaquinaDTO::fromModel)
        .toList();
    }

    public MaquinaDTO buscarPorId(Long id){
        return MaquinaDTO.fromModel(obtenerMaquina(id));
    }

    public MaquinaDTO crear(MaquinaDTO dto){
        Maquina guardada = maquinaRepository.save(dto.toModel());
        log.info("Maquina creada id={} nombre={}", guardada.getId(), guardada.getNombre());
        return MaquinaDTO.fromModel(guardada);
    }

    public MaquinaDTO actualizar(Long id, MaquinaDTO dto){
        Maquina maquina = obtenerMaquina(id);

        maquina.setNombre(dto.getNombre());
        maquina.setTipo(dto.getTipo());
        maquina.setUbicacion(dto.getUbicacion());
        maquina.setEstado(dto.getEstado());
        maquina.setCostoPorBloque(dto.getCostoPorBloque());
        maquina.setFechaInstalacion(dto.getFechaInstalacion());

        Maquina act = maquinaRepository.save(maquina);
        log.info("Maquina actualizada id={}", id);
        return MaquinaDTO.fromModel(act);
    }

    public void eliminar(Long id){
        if (!maquinaRepository.existsById(id)){
            throw new ResourceNotFoundException("Maquina no encontrada con id " + id);
        }
        maquinaRepository.deleteById(id);
        log.info("Maquina eliminada id={}", id);
        
    }

    public boolean estaActiva(Long id){
        Maquina m = obtenerMaquina(id);
        return "ACTIVA".equalsIgnoreCase(m.getEstado());
    }

    public boolean existePorId(Long id){
        return maquinaRepository.existsById(id);
    }

    public List<MaquinaDTO> buscarPorEstado(String estado){
        return maquinaRepository.findByEstado(estado.toUpperCase())
        .stream()
        .map(MaquinaDTO::fromModel)
        .toList();
    }

    public List<MaquinaDTO> buscarPorTipo(String tipo){
        return maquinaRepository.findByTipo(tipo.toUpperCase())
        .stream()
        .map(MaquinaDTO::fromModel)
        .toList();
    }

    public long totalMaquinas(){
        return maquinaRepository.count();
    }

    private Maquina obtenerMaquina(Long id){
        return maquinaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Maquina no encontrada con id " + id));
    }
}
