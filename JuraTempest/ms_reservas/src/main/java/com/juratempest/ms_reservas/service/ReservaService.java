package com.juratempest.ms_reservas.service;

import java.time.LocalDate;
import java.util.List;


import org.springframework.stereotype.Service;

import com.juratempest.ms_reservas.client.ReservaClient;
import com.juratempest.ms_reservas.dto.ReservaDTO;
import com.juratempest.ms_reservas.exception.BadRequestException;
import com.juratempest.ms_reservas.exception.ResourceNotFoundException;
import com.juratempest.ms_reservas.model.Reserva;
import com.juratempest.ms_reservas.repository.ReservaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ReservaClient reservaClient;

    public ReservaService(ReservaRepository reservaRepository, ReservaClient reservaClient){
        this.reservaRepository = reservaRepository;
        this.reservaClient = reservaClient;
    }

    public List<ReservaDTO> listar(){
        return reservaRepository.findAll()
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    public ReservaDTO buscarPorId(Long id){
        return ReservaDTO.fromModel(obtenerReserva(id));
    }

    public ReservaDTO crear(ReservaDTO dto){
        validarDatos(dto);
        if(reservaRepository.existsByMaquinaIdAndHorarioId(dto.getMaquinaId(),dto.getHorarioId())){
            log.warn("Intento de reserva duplicada maquinaId={} horarioId={}", dto.getMaquinaId(), dto.getHorarioId());
            throw new ResourceNotFoundException("La maquina ya esta reservada en este horario");
        }
        dto.setFechaReserva(LocalDate.now());
        dto.setEstado("ACTIVA");
        Reserva guardada = reservaRepository.save(dto.toModel());
        log.info("Reserva creada id = {}",guardada.getId());
        return ReservaDTO.fromModel(guardada);
    }

    public ReservaDTO actualizar(Long id, ReservaDTO dto){

        Reserva reserva = obtenerReserva(id);
        validarDatos(dto);

        if (reservaRepository.existsByMaquinaIdAndHorarioIdAndIdNot(dto.getMaquinaId(), dto.getHorarioId(), id)){
            log.warn("Intento de reserva duplicada maquinaId={} horarioId={}", dto.getMaquinaId(), dto.getHorarioId());
            throw new BadRequestException("La maquina ya esta reservada en el horario seleccionado"); 
        }

        reserva.setUsuarioId(dto.getUsuarioId());
        reserva.setMaquinaId(dto.getMaquinaId());
        reserva.setHorarioId(dto.getHorarioId());
        
        if (dto.getEstado() != null){
            reserva.setEstado(dto.getEstado().toUpperCase());
        }

        Reserva actualizada = reservaRepository.save(reserva);

        log.info("Reserva actualizada id={}", id);

        return ReservaDTO.fromModel(actualizada);
    }

    public void eliminar(Long id){

        if(!reservaRepository.existsById(id)){
            log.warn("Intento de eliminar reserva no existente id={}", id);
            throw new ResourceNotFoundException(
                "Reserva no encontrada con id " + id
            );
        }

        reservaRepository.deleteById(id);

        log.info("Reserva eliminada id={}", id);
    }

    public List<ReservaDTO> buscarPorUsuario(Long usuarioId){

        return reservaRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    public List<ReservaDTO> buscarPorEstado(String estado){

        return reservaRepository.findByEstado(estado.toUpperCase())
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    public long totalReservas(){
        return reservaRepository.count();
    }

    private Reserva obtenerReserva(Long id){

        return reservaRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Reserva no encontrada con id " + id
                ));
    }

    //METODO PRIVADO PARA VALIDAR CONTENIDO DE CONSULTAS
    private void validarDatos(ReservaDTO dto){
        if (!reservaClient.usuarioExiste(dto.getUsuarioId())){
            throw new ResourceNotFoundException("USUARIO NO EXISTE");
        }
        
        if (!reservaClient.maquinaActiva(dto.getMaquinaId())){
            throw new ResourceNotFoundException("MAQUINA ESTA BLOQUEADA");
        }
        
        if (!reservaClient.bloqueExiste(dto.getHorarioId())){
            throw new ResourceNotFoundException("BLOQUE DE HORARIO NO EXISTE");
        }
    }
}
