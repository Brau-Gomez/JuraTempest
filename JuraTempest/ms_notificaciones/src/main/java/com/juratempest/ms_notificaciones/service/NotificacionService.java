package com.juratempest.ms_notificaciones.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_notificaciones.client.UsuarioClient;
import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.exception.BadRequestException;
import com.juratempest.ms_notificaciones.exception.ResourceNotFoundException;
import com.juratempest.ms_notificaciones.model.Notificacion;
import com.juratempest.ms_notificaciones.repository.NotificacionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificacionService {

    private final NotificacionRepository repository;
    private final UsuarioClient usuarioClient;

    public NotificacionService(NotificacionRepository repository, UsuarioClient usuarioClient) {
        this.repository = repository;
        this.usuarioClient = usuarioClient;
    }
    //LISTAR
    public List<NotificacionDTO> listar() {
        return repository.findAll().stream().map(NotificacionDTO::fromModel).toList();
    }
    //BUSCAR POR ID
    public NotificacionDTO buscarPorId(Long id) {
        return NotificacionDTO.fromModel(obtenerNotificacion(id));
    }
    //BUSCAR POR USUARIO
    public List<NotificacionDTO> buscarPorUsuario(Long usuarioId) {
        if (usuarioId == null){
            log.warn("Usuario vacio, ingrese nuevamente");
            throw new BadRequestException("El usuario no puede estar vacio, Ingrese su nombre de usuario nuevamente");
        }
        else if (!usuarioClient.usuarioExiste(usuarioId)){
            log.warn("Usuario inexistente");
            throw new BadRequestException("Usuario no existe");
        
        }
        return repository.findByUsuarioId(usuarioId).stream().map(NotificacionDTO::fromModel).toList();
        
    }
    //BUSCAR NO LEIDAS POR USUARIO
    public List<NotificacionDTO> buscarNoLeidasPorUsuario(Long usuarioId) {
        if (usuarioId == null){
            log.warn("Usuario vacio, no se encontraron notificaciones");
            throw new BadRequestException("Usuario vacio, favor ingresar usuario para consultar notifiaciones");
        }
        else if (!usuarioClient.usuarioExiste(usuarioId)){
            log.warn("Usuario inexistente");
            throw new BadRequestException("Usuario no existe");
        
        }
        return repository.findByUsuarioIdAndLeidaFalse(usuarioId).stream().map(NotificacionDTO::fromModel).toList();
    }
    //BUSCAR POR TIPO
    public List<NotificacionDTO> buscarPorTipo(String tipo) {
        if (tipo == null){
            log.warn("Tipo vacio, no se encontraron notificaciones");
            throw new BadRequestException("Tipo vacio, favor ingresar tipo para consultar notifiaciones");
        }
        tipo = tipo.trim().toUpperCase();
        if(!List.of("RESERVA", "PAGO", "MANTENIMIENTO", "TORNEO","PROMOCION", "SISTEMA").contains(tipo)){
            throw new BadRequestException("Tipo no valido, Favor intentar con uno de los siguientes: RESERVA, PAGO, MANTENIMIENTO, TORNEO, PROMOCION, SISTEMA");
        }
        return repository.findByTipo(tipo).stream().map(NotificacionDTO::fromModel).toList();
    }
    //BUSCAR POR CANAL
    public Long totalNoLeidasPorUsuario(Long usuarioId) {
        if (usuarioId == null){
            log.warn("Usuario vacio, no se encontraron notificaciones");
            throw new BadRequestException("Usuario no puede estar vacio, Ingrese el usuario para consultar las notificaciones");
        }       
        else if (!usuarioClient.usuarioExiste(usuarioId)){
            log.warn("Usuario inexistente");
            throw new BadRequestException("Usuario no existe");
        
        }
        return repository.countByUsuarioIdAndLeidaFalse(usuarioId);
    }
    //CREAR
    public NotificacionDTO crear(NotificacionDTO dto) {
        if (dto == null || dto.getTipo() == null || dto.getCanal() == null){
            throw new BadRequestException("Los campos tipo y canal son obligatorios");
        }

        dto.setTipo(dto.getTipo().trim().toUpperCase());
        dto.setCanal(dto.getCanal().trim().toUpperCase());

        validarDatos(dto);

        dto.setLeida(false);
        dto.setFechaCreacion(LocalDateTime.now());

        Notificacion notificacion = repository.save(dto.toModel());
        log.info("Notificacion creada id={}", notificacion.getId());

        return NotificacionDTO.fromModel(notificacion);
    }
    //ACTUALIZAR
    public NotificacionDTO marcarComoLeida(Long id) {

        Notificacion noLeida = obtenerNotificacion(id);
        if (noLeida.getLeida()){
            throw new BadRequestException("Notificacion ya marcada como leida");
        }
        noLeida.setLeida(true);
        Notificacion leida = repository.save(noLeida);
        log.info("Notificacion marcada como leida id={}", id);
        return NotificacionDTO.fromModel(leida);

    }
    //MARCAR TODAS COMO LEIDAS
    public void marcarTodasComoLeidas(Long usuarioId) {
        if (usuarioId == null ){
            throw new BadRequestException("EL usuario no puede estar vacio. Favor ingresar usuario");
        }
        else if (!usuarioClient.usuarioExiste(usuarioId)){
            throw new BadRequestException("Usuario no existe");
        }

        List<Notificacion> notificaciones = repository.findByUsuarioIdAndLeidaFalse(usuarioId);
        for (Notificacion notificacion : notificaciones){
            notificacion.setLeida(true);
        }

        repository.saveAll(notificaciones);
        log.info("Notificaciones marcadas como leidas usuarioId={}", usuarioId);
    }
    //ELIMINAR
    public void eliminar(Long id) {
        Notificacion notificacion = obtenerNotificacion(id);
        if (!notificacion.getLeida()){
            throw new BadRequestException("No se puede eliminar una notificacion no leida");
        }

        repository.deleteById(id);
        log.info("Notificacion eliminada id={}", id);
    }

    //BUSCAR SOLO LAS NO LEIDAS
    public List<NotificacionDTO> obtenerNoLeidas(){
        return repository.findByLeidaFalse().stream().map(NotificacionDTO::fromModel).toList();
    }


    //METODOS PRIVADOS
    //Confirma existensia de notificaciones y valida datos de notificacion.
    private Notificacion obtenerNotificacion(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Notificacion no encontrada con id " + id));
    }

    private void validarDatos(NotificacionDTO dto) {

        if (dto == null 
            || dto.getUsuarioId() == null
            || dto.getTipo() == null || dto.getTipo().isBlank()
            || dto.getCanal() == null || dto.getCanal().isBlank()
            || dto.getTitulo() == null || dto.getTitulo().isBlank()
            || dto.getMensaje() == null || dto.getMensaje().isBlank()) {
            throw new BadRequestException("Faltan datos. Favor revise los campos");
        }

        if (!usuarioClient.usuarioExiste(dto.getUsuarioId())){
            throw new ResourceNotFoundException("USUARIO NO EXISTE");
        }

        List<String> tipos = List.of(
            "RESERVA", 
            "PAGO", 
            "MANTENIMIENTO", 
            "TORNEO", 
            "PROMOCION", 
            "SISTEMA"
        );
        if (!tipos.contains(dto.getTipo())){
            throw new BadRequestException("Tipo no valido, Ingrese uno de los siguientes: RESERVA, PAGO, MANTENIMIENTO, TORNEO, PROMOCION, SISTEMA");
        }
        
        List<String> tipos_canal = List.of(
            "SISTEMA",
            "EMAIL", 
            "SMS_SIMULADO", 
            "WHATSAPP_SIMULADO"
        );

        if (!tipos_canal.contains(dto.getCanal())){
            throw new BadRequestException("Canal no valido. Ingrese uno de los siguientes: SISTEMA, EMAIL, SMS_SIMULADO, WHATSAPP_SIMULADO");
        }

    }
}
