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

    // Constructor usado por Spring para inyectar el repositorio de maquinas.
    // El service depende del repositorio para leer y escribir datos de la base.
    public MaquinaService(MaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    // Lista todas las maquinas y las transforma a DTO.
    // Separar entidad y DTO evita exponer detalles internos de persistencia en la API.
    public List<MaquinaDTO> listar(){
        return maquinaRepository.findAll()
        .stream()
        .map(MaquinaDTO::fromModel)
        .toList();
    }

    // Busca una maquina por id.
    // Reutiliza obtenerMaquina para mantener una sola forma de manejar el caso no encontrado.
    public MaquinaDTO buscarPorId(Long id){
        return MaquinaDTO.fromModel(obtenerMaquina(id));
    }

    // Crea una maquina nueva desde los datos recibidos en el DTO.
    // Guardamos la entidad y devolvemos DTO para mantener consistente el contrato de la API.
    public MaquinaDTO crear(MaquinaDTO dto){
        Maquina guardada = maquinaRepository.save(dto.toModel());
        log.info("Maquina creada id={} nombre={}", guardada.getId(), guardada.getNombre());
        return MaquinaDTO.fromModel(guardada);
    }

    // Actualiza una maquina existente campo por campo.
    // Primero buscamos la entidad para no crear accidentalmente un registro nuevo con otro id.
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

    // Elimina una maquina por id.
    // Verificamos existencia para devolver un error claro cuando el recurso no existe.
    public void eliminar(Long id){
        if (!maquinaRepository.existsById(id)){
            throw new ResourceNotFoundException("Maquina no encontrada con id " + id);
        }
        maquinaRepository.deleteById(id);
        log.info("Maquina eliminada id={}", id);
        
    }

    // Indica si una maquina esta activa.
    // Se usa una comparacion ignorando mayusculas para tolerar variaciones del texto almacenado.
    public boolean estaActiva(Long id){
        Maquina m = obtenerMaquina(id);
        return "ACTIVA".equalsIgnoreCase(m.getEstado());
    }

    // Verifica existencia sin cargar todos los datos de la maquina.
    // Es una consulta liviana para validaciones entre servicios.
    public boolean existePorId(Long id){
        return maquinaRepository.existsById(id);
    }

    // Busca maquinas por estado normalizando el valor a mayusculas.
    // Esto mantiene consultas consistentes con estados guardados como valores controlados.
    public List<MaquinaDTO> buscarPorEstado(String estado){
        return maquinaRepository.findByEstado(estado.toUpperCase())
        .stream()
        .map(MaquinaDTO::fromModel)
        .toList();
    }

    // Busca maquinas por tipo normalizando el texto recibido.
    // Entrega un filtro especifico sin trasladar esa responsabilidad al cliente.
    public List<MaquinaDTO> buscarPorTipo(String tipo){
        return maquinaRepository.findByTipo(tipo.toUpperCase())
        .stream()
        .map(MaquinaDTO::fromModel)
        .toList();
    }

    // Cuenta todas las maquinas registradas.
    // Delegamos en JpaRepository.count para que la base de datos realice el conteo eficientemente.
    public long totalMaquinas(){
        return maquinaRepository.count();
    }

    // Obtiene la entidad Maquina o lanza una excepcion si no existe.
    // Este metodo privado evita duplicar findById y mensajes de error.
    private Maquina obtenerMaquina(Long id){
        return maquinaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Maquina no encontrada con id " + id));
    }
}
