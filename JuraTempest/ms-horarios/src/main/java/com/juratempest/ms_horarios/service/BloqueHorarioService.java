package com.juratempest.ms_horarios.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_horarios.dto.BloquehorarioDTO;
import com.juratempest.ms_horarios.exception.BadRequestException;
import com.juratempest.ms_horarios.exception.ResourceNotFoundException;
import com.juratempest.ms_horarios.model.BloqueHorario;
import com.juratempest.ms_horarios.repository.BloqueHorarioRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BloqueHorarioService {

    private final BloqueHorarioRepository bloqueHorarioRepository;

    // Constructor usado por Spring para inyectar el repositorio de bloques horarios.
    // El service concentra reglas de negocio y usa el repositorio solo para persistencia.
    public BloqueHorarioService(BloqueHorarioRepository bloqueHorarioRepository){
        this.bloqueHorarioRepository = bloqueHorarioRepository;
    }

    // Lista todos los bloques horarios y los convierte a DTO.
    // Devolver DTO evita exponer directamente la entidad JPA hacia la API.
    public List<BloquehorarioDTO> listar(){
        return bloqueHorarioRepository.findAll()
        .stream()
        .map(BloquehorarioDTO::fromModel)
        .toList();
    }
    
    // Busca un bloque horario por id.
    // Reutiliza obtenerBloque para mantener un manejo uniforme del caso no encontrado.
    public BloquehorarioDTO buscarPorId(Long id){
        return BloquehorarioDTO.fromModel(obtenerBloque(id));
    }

    // Crea un bloque horario despues de validar reglas del dominio.
    // Validamos orden de horas, cupos y solapamientos para evitar horarios incoherentes.
    public BloquehorarioDTO crear(BloquehorarioDTO dto, Long id){
        validarHorario(dto);
        validarCupos(dto);
        validarRangoFechas(dto,id);
        if (!dto.getHoraFin().isAfter(dto.getHoraInicio())){
            throw new BadRequestException("La hora de fin no puede ser posterior a la fecha de inicio");
        }
        BloqueHorario guardado = bloqueHorarioRepository.save(dto.toModel());
        log.info("BloqueHorario creado id={} fecha={} horaInicio={} horaFin={} ",
            guardado.getId(), 
            guardado.getFecha(), 
            guardado.getHoraInicio(), 
            guardado.getHoraFin()
        );

        return BloquehorarioDTO.fromModel(guardado);
    }

    // Actualiza un bloque horario existente aplicando las mismas validaciones de creacion.
    // Primero cargamos la entidad para asegurar que el id exista antes de modificar campos.
        public BloquehorarioDTO actualizar(Long id, BloquehorarioDTO dto) {
        BloqueHorario bloque = obtenerBloque(id);
        validarCupos(dto);
        validarHorario(dto);
        validarRangoFechas(dto,id);


        bloque.setFecha(dto.getFecha());
        bloque.setHoraInicio(dto.getHoraInicio());
        bloque.setHoraFin(dto.getHoraFin());
        bloque.setEstado(dto.getEstado());
        bloque.setDisponible(dto.getDisponible());
        bloque.setCapacidadMaquina(dto.getCapacidadMaquina());
        bloque.setCuposDisponibles(dto.getCuposDisponibles());        

        BloqueHorario actualizado = bloqueHorarioRepository.save(bloque);

        log.info("Bloque horario actualizado id={}", id);

        return BloquehorarioDTO.fromModel(actualizado);
    }

    // Elimina un bloque horario por id.
    // Verificamos existencia para responder correctamente si el bloque no existe.
    public void eliminar(Long id){
        if (!bloqueHorarioRepository.existsById(id)){
            throw new ResourceNotFoundException("BloqueHorario no encontrado con id " + id);
        }
        bloqueHorarioRepository.deleteById(id);
        log.info("BloqueHorario eliminado id={}", id);
    }

    // Verifica si existe un bloque horario sin cargar toda la entidad.
    // Este metodo es util para validaciones rapidas desde otros microservicios.
        public boolean existePorId(Long id) {
        return bloqueHorarioRepository.existsById(id);
    }

    // Busca bloques por fecha exacta.
    // Permite consultar la agenda de un dia especifico.
    public List<BloquehorarioDTO> buscarPorFecha(LocalDate fecha) {
        return bloqueHorarioRepository.findByFecha(fecha)
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    // Busca bloques disponibles.
    // Entrega solo horarios reservables para simplificar el trabajo del cliente.
    public List<BloquehorarioDTO> buscarDisponibles() {
        return bloqueHorarioRepository.findByDisponibleTrue()
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    // Busca bloques por estado normalizando a mayusculas.
    // Esto mantiene consultas consistentes con estados guardados como valores controlados.
    public List<BloquehorarioDTO> buscarPorEstado(String estado) {
        return bloqueHorarioRepository.findByEstado(estado.toUpperCase())
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    // Busca bloques dentro de un rango de fechas.
    // Antes valida que la fecha inicial no sea posterior a la fecha final.
    public List<BloquehorarioDTO> buscarPorRango(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        return bloqueHorarioRepository.findByFechaBetween(inicio, fin)
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    // Cuenta todos los bloques horarios registrados.
    // Delegamos en el repositorio para que la base de datos realice el conteo.
    public long totalBloques() {
        return bloqueHorarioRepository.count();
    }









    // Obtiene un bloque horario por id o lanza una excepcion si no existe.
    // Centralizar la busqueda evita repetir findById y mensajes de error.
    private BloqueHorario obtenerBloque(Long id) {
        return bloqueHorarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("BloqueHorario no encontrado con id " + id));
    }

    // Valida que la hora de inicio sea anterior a la hora de fin.
    // Esta regla evita crear bloques con duracion negativa o imposible.
    private void validarHorario(BloquehorarioDTO dto){
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())){
            throw new BadRequestException("La hora de inicio no puede ser posterior a la hora de fin");
        }
    }

    // Valida que los cupos disponibles no superen la capacidad maxima.
    // Mantiene coherencia entre disponibilidad y capacidad real del bloque.
    private void validarCupos(BloquehorarioDTO dto){
        if(dto.getCuposDisponibles() > dto.getCapacidadMaquina()){
            throw new BadRequestException("Los cupos disponibles no pueden ser mayores a la capacidad de la máquina");
        }
    }

    // Valida que no exista otro bloque en la misma fecha con horario solapado.
    // Al actualizar, ignora el mismo id para no detectar conflicto contra el propio registro.
    private void validarRangoFechas(BloquehorarioDTO dto, Long id){
        List<BloqueHorario> bloquesEnRango = bloqueHorarioRepository.findByFechaBetween(dto.getFecha(), dto.getFecha());
        for (BloqueHorario bloque : bloquesEnRango) {
            if (id != null && bloque.getId().equals(id)) {
                continue; 
            }

            boolean errorRango = bloque.getHoraInicio().isBefore(dto.getHoraFin()) &&
                                bloque.getHoraFin().isAfter(dto.getHoraInicio());
             if (errorRango) {
                throw new BadRequestException("Ya existe un bloque horario en el mismo rango de tiempo");
            }
        }
    }
}



