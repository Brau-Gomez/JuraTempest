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

    // Constructor usado por Spring para inyectar repositorio y cliente de validacion externa.
    // ReservaService coordina datos locales con consultas a usuarios, maquinas y horarios.
    public ReservaService(ReservaRepository reservaRepository, ReservaClient reservaClient){
        this.reservaRepository = reservaRepository;
        this.reservaClient = reservaClient;
    }

    // Lista todas las reservas y las convierte a DTO.
    // Asi la API no expone directamente la entidad JPA.
    public List<ReservaDTO> listar(){
        return reservaRepository.findAll()
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    // Busca una reserva por id.
    // Reutiliza obtenerReserva para manejar de forma uniforme el caso no encontrado.
    public ReservaDTO buscarPorId(Long id){
        return ReservaDTO.fromModel(obtenerReserva(id));
    }

    // Crea una reserva nueva validando referencias y disponibilidad.
    // Evita guardar reservas con usuario inexistente, maquina bloqueada, horario inexistente o duplicidad.
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

    // Actualiza una reserva existente y revisa que no choque con otra reserva.
    // La consulta con IdNot permite ignorar la reserva actual durante la validacion de duplicados.
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

    // Elimina una reserva por id.
    // Confirmar existencia antes de borrar permite responder 404 si el id no pertenece a ninguna reserva.
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

    // Busca reservas asociadas a un usuario.
    // La consulta filtrada reduce trabajo del cliente y entrega directamente el historial necesario.
    public List<ReservaDTO> buscarPorUsuario(Long usuarioId){

        return reservaRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    // Busca reservas por estado normalizando a mayusculas.
    // Esto mantiene consistencia con los estados controlados del DTO.
    public List<ReservaDTO> buscarPorEstado(String estado){

        return reservaRepository.findByEstado(estado.toUpperCase())
            .stream()
            .map(ReservaDTO::fromModel)
            .toList();
    }

    // Cuenta todas las reservas guardadas.
    // Delegamos el conteo al repositorio para que la base de datos haga el trabajo.
    public long totalReservas(){
        return reservaRepository.count();
    }

    // Obtiene una reserva o lanza excepcion si no existe.
    // Centralizar esta busqueda evita repetir findById y mensajes de error.
    private Reserva obtenerReserva(Long id){

        return reservaRepository.findById(id)
            .orElseThrow(() ->
                new ResourceNotFoundException(
                    "Reserva no encontrada con id " + id
                ));
    }

    // Valida las referencias externas necesarias para crear o actualizar una reserva.
    // Esta regla protege la consistencia entre microservicios antes de guardar datos locales.
    private void validarDatos(ReservaDTO dto){
        if (!reservaClient.usuarioExiste(dto.getUsuarioId())){
            log.warn("Reserva rechazada: usuario inexistente usuarioId={}", dto.getUsuarioId());
            throw new ResourceNotFoundException("USUARIO NO EXISTE");
        }
        
        if (!reservaClient.maquinaActiva(dto.getMaquinaId())){
            log.warn("Reserva rechazada: maquina no activa maquinaId={}", dto.getMaquinaId());
            throw new ResourceNotFoundException("MAQUINA ESTA BLOQUEADA");
        }
        
        if (!reservaClient.bloqueExiste(dto.getHorarioId())){
            log.warn("Reserva rechazada: bloque horario inexistente horarioId={}", dto.getHorarioId());
            throw new ResourceNotFoundException("BLOQUE DE HORARIO NO EXISTE");
        }
    }
}
