package com.juratempest.ms_reservas.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_reservas.client.ReservaClient;
import com.juratempest.ms_reservas.dto.ReservaDTO;
import com.juratempest.ms_reservas.exception.ResourceNotFoundException;
import com.juratempest.ms_reservas.model.Reserva;
import com.juratempest.ms_reservas.repository.ReservaRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaClient reservaClient;

    public ReservaService(
        ReservaRepository reservaRepository,
        ReservaClient reservaClient
    ){
        this.reservaRepository = reservaRepository;
        this.reservaClient = reservaClient;
    }

    public List<ReservaDTO> listar(){
        return reservaRepository.findAll()
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    public ReservaDTO crear(ReservaDTO dto){

        if(!reservaClient.usuarioExiste(dto.getUsuarioId())){
            throw new ResourceNotFoundException("Usuario no existe");
        }

        if(!reservaClient.maquinaActiva(dto.getMaquinaId())){
            throw new ResourceNotFoundException("Maquina no activa");
        }

        if(!reservaClient.bloqueExiste(dto.getHorarioId())){
            throw new ResourceNotFoundException("Bloque horario no existe");
        }

        if(reservaRepository.existsByMaquinaIdAndHorarioId(
            dto.getMaquinaId(),
            dto.getHorarioId()
        )){
            throw new ResourceNotFoundException("La maquina ya esta reservada");
        }

        dto.setFechaReserva(LocalDate.now());
        dto.setEstado("ACTIVA");

        Reserva guardada = reservaRepository.save(dto.toModel());

        log.info("Reserva creada id={}", guardada.getId());

        return ReservaDTO.fromModel(guardada);
    }

    public List<ReservaDTO> buscarPorUsuario(Long usuarioId){
        return reservaRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }
}