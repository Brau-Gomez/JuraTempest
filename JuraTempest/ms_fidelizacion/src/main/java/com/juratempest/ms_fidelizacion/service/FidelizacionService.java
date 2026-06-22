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

    // Constructor usado por Spring para inyectar repositorio y cliente de usuarios.
    // El service necesita ambos porque combina persistencia local con validacion en otro microservicio.
    public FidelizacionService(FidelizacionRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
    }

    // Lista todos los registros y los transforma a DTO.
    // No devolvemos entidades directamente para mantener separada la capa de API de la capa JPA.
    public List<FidelizacionDTO> listar() {

        return repository.findAll()
                .stream()
                .map(FidelizacionDTO::fromModel)
                .toList();
    }

    // Busca un registro por id y lanza excepcion si no existe.
    // Esto mantiene una respuesta 404 clara en vez de devolver null al controlador.
    public FidelizacionDTO buscarPorId(Long id) {

        Fidelizacion fidelizacion = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Registro no encontrado"));

        return FidelizacionDTO.fromModel(fidelizacion);
    }

    // Obtiene el historial de puntos de un usuario.
    // La consulta filtrada evita traer todos los registros cuando solo interesa un usuario especifico.
    public List<FidelizacionDTO> buscarPorUsuario(Long usuarioId) {

        return repository.findByUsuarioId(usuarioId)
                .stream()
                .map(FidelizacionDTO::fromModel)
                .toList();
    }

    // Crea un registro de puntos para un usuario existente.
    // Primero validamos contra ms_usuarios para no guardar puntos asociados a usuarios inexistentes.
    public FidelizacionDTO crear(FidelizacionDTO dto) {
        if (!usuarioClient.usuarioExiste(dto.getUsuarioId())){
            log.warn("Intento de crear fidelizacion para usuario inexistente usuarioId={}", dto.getUsuarioId());
            throw new ResourceNotFoundException("Usuario no existe");
        }
        Fidelizacion fidelizacion = dto.toModel();

        fidelizacion.setFechaRegistro(LocalDate.now());
        Fidelizacion guardada = repository.save(fidelizacion);
        log.info("Registro de fidelizacion creado id={} usuarioId={}", guardada.getId(), guardada.getUsuarioId());
        return FidelizacionDTO.fromModel(guardada);
        
    }

    // Elimina un registro de fidelizacion por id.
    // Verificar existencia antes de borrar permite entregar un error de negocio comprensible.
    public void eliminar(Long id) {

        if (!repository.existsById(id)) {
            log.warn("Intento de eliminar fidelizacion inexistente id={}", id);
            throw new ResourceNotFoundException("Registro no encontrado");
        }

        repository.deleteById(id);
        log.info("Registro eliminado con exito id={}" , id);
    }

    // Suma todos los puntos acumulados por un usuario.
    // Usamos stream y mapToLong para transformar cada registro en su puntaje y calcular el total.
    public Long totalPuntos(Long usuarioId) {
        
        return repository.findByUsuarioId(usuarioId)
                .stream()
                .mapToLong(Fidelizacion::getPuntos)
                .sum();
    }
    
    // Actualiza un registro de puntos ya existente.
    // Se vuelve a validar el usuario para evitar que la actualizacion apunte a una referencia invalida.
    public FidelizacionDTO actualizar(Long id, FidelizacionDTO dto){

        if (!usuarioClient.usuarioExiste(dto.getUsuarioId())){
            log.warn("Intento de actualizar fidelizacion con usuario inexistente id={} usuarioId={}", id, dto.getUsuarioId());
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
