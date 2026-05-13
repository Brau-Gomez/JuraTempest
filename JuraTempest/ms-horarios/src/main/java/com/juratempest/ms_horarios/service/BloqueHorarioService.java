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
    public BloqueHorarioService(BloqueHorarioRepository bloqueHorarioRepository){
        this.bloqueHorarioRepository = bloqueHorarioRepository;
    }

    public List<BloquehorarioDTO> listar(){
        return bloqueHorarioRepository.findAll()
        .stream()
        .map(BloquehorarioDTO::fromModel)
        .toList();
    }
    
    public BloquehorarioDTO buscarPorId(Long id){
        return BloquehorarioDTO.fromModel(obtenerBloque(id));
    }

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

    public void eliminar(Long id){
        if (!bloqueHorarioRepository.existsById(id)){
            throw new ResourceNotFoundException("BloqueHorario no encontrado con id " + id);
        }
        bloqueHorarioRepository.deleteById(id);
        log.info("BloqueHorario eliminado id={}", id);
    }

        public boolean existePorId(Long id) {
        return bloqueHorarioRepository.existsById(id);
    }

    public List<BloquehorarioDTO> buscarPorFecha(LocalDate fecha) {
        return bloqueHorarioRepository.findByFecha(fecha)
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    public List<BloquehorarioDTO> buscarDisponibles() {
        return bloqueHorarioRepository.findByDisponibleTrue()
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    public List<BloquehorarioDTO> buscarPorEstado(String estado) {
        return bloqueHorarioRepository.findByEstado(estado.toUpperCase())
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    public List<BloquehorarioDTO> buscarPorRango(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        return bloqueHorarioRepository.findByFechaBetween(inicio, fin)
            .stream()
            .map(BloquehorarioDTO::fromModel)
            .toList();
    }

    public long totalBloques() {
        return bloqueHorarioRepository.count();
    }









    //METODOS PRIVADO PARA FACILITAR BUSQUEDA 
    private BloqueHorario obtenerBloque(Long id) {
        return bloqueHorarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("BloqueHorario no encontrado con id " + id));
    }

    private void validarHorario(BloquehorarioDTO dto){
        if (!dto.getHoraInicio().isBefore(dto.getHoraFin())){
            throw new BadRequestException("La hora de inicio no puede ser posterior a la hora de fin");
        }
    }

    private void validarCupos(BloquehorarioDTO dto){
        if(dto.getCuposDisponibles() > dto.getCapacidadMaquina()){
            throw new BadRequestException("Los cupos disponibles no pueden ser mayores a la capacidad de la máquina");
        }
    }

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



