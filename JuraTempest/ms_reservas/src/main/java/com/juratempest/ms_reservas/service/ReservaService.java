package com.juratempest.ms_reservas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_reservas.dto.ReservaDTO;
import com.juratempest.ms_reservas.exception.ResourceNotFoundException;
import com.juratempest.ms_reservas.model.Reserva;
import com.juratempest.ms_reservas.repository.ReservaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservaService {
    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository){
        this.reservaRepository = reservaRepository;
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
        if(reservaRepository.existsByMaquinaIdAndHorarioId(dto.getMaquinaId(),dto.getHorarioId())){
            throw new ResourceNotFoundException("La maquina ya esta reservada en este horario");
        }
        dto.setFechaReserva(LocalDate.now());
        dto.setEstado("Activa");
        Reserva guardada = reservaRepository.save(dto.toModel());
        log.info("Reserva creada id = {}",guardada.getId());
        return ReservaDTO.fromModel(guardada);
    }

    public ReservaDTO actualizar(Long id, ReservaDTO dto){

        Reserva reserva = obtenerReserva(id);

        reserva.setUsuarioId(dto.getUsuarioID());
        reserva.setMaquinaId(dto.getMaquinaId());
        reserva.setHorarioId(dto.getHorarioId());
        reserva.setEstado(dto.getEstado());

        Reserva actualizada = reservaRepository.save(reserva);

        log.info("Reserva actualizada id={}", id);

        return ReservaDTO.fromModel(actualizada);
    }

    public void eliminar(Long id){

        if(!reservaRepository.existsById(id)){
            throw new ResourceNotFoundException(
                "Reserva no encontrada con id " + id
            );
        }

        reservaRepository.deleteById(id);

        log.info("Reserva eliminada id={}", id);
    }

    public List<ReservaDTO> buscarPorUsuario(Long usuarioId){

        return reservaRepository.findByusuarioId(usuarioId)
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
}
