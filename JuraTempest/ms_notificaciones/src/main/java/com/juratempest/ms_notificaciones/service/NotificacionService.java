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
        log.info("Listando todas las notificaciones");

        List<NotificacionDTO> notificaciones = repository.findAll()
            .stream()
            .map(NotificacionDTO::fromModel)
            .toList();
        log.info("Total de notificaciones encontradas={}", notificaciones.size());
        
        return notificaciones;
    }


    //BUSCAR POR ID
    public NotificacionDTO buscarPorId(Long id) {
        log.info("Buscando notificaciones id={}", id);
        Notificacion notificacion = obtenerNotificacion(id);
        log.info("Notificacion encontrada id={}", id);

        return NotificacionDTO.fromModel(notificacion);

    }


    //BUSCAR POR USUARIO
    public List<NotificacionDTO> buscarPorUsuario(Long usuarioId) {
        log.info("Buscando notificaciones por usuarioId={}", usuarioId);

        validarUsuario(usuarioId);

        List<NotificacionDTO> notificaciones = repository.findByUsuarioId(usuarioId)
            .stream()
            .map(NotificacionDTO::fromModel)
            .toList();
        

            log.info("Total de notificaciones usuarioId={} total={}", usuarioId, notificaciones.size());
        return notificaciones;
    }


    //BUSCAR NO LEIDAS POR USUARIO
    public List<NotificacionDTO> buscarNoLeidasPorUsuario(Long usuarioId) {
        log.info("Buscando notificaciones no leidas usuarioId={}", usuarioId);

        validarUsuario(usuarioId);


        List<NotificacionDTO> notificaciones = repository.findByUsuarioIdAndLeidaFalse(usuarioId)
        .stream().map(NotificacionDTO::fromModel).toList();

        log.info("Total de notificaciones no leidas usuarioId={} total={}", usuarioId, notificaciones.size());
        return notificaciones;
    }

    //BUSCAR POR TIPO
    public List<NotificacionDTO> buscarPorTipo(String tipo) {
        log.info("Buscando notificacion por tipo={}", tipo);
        tipo = normalizarTipo(tipo);

        List<NotificacionDTO> notificaciones = repository.findByTipo(tipo)
            .stream()
            .map(NotificacionDTO::fromModel)
            .toList();
        
            log.info("Total de notificaciones tipo={} total={}", tipo, notificaciones.size());

            return notificaciones;
    }


    
    //BUSCAR POR CANAL
    public Long totalNoLeidasPorUsuario(Long usuarioId) {
        log.info("Calculando total de notificaciones no leidas usuarioId={}", usuarioId);

        validarUsuario(usuarioId);

        Long total = repository.countByUsuarioIdAndLeidaFalse(usuarioId);
        
        log.info("Total de notificaciones no leidas usuarioId={} total={}", usuarioId, total);
        return total;
    }


    //CREAR
    public NotificacionDTO crear(NotificacionDTO dto) {
        log.info("Creando notificacion usuarioId={}", dto!= null ? dto.getUsuarioId() : null);
        
        if(dto == null){
            log.warn("Intento de crear notificacion con cuerpo vacio");
            throw new BadRequestException("El cuerpo de la notificacion es obligatorio");

        }

        dto.setCanal(normalizarCanal(dto.getCanal()));
        dto.setTipo(normalizarTipo(dto.getTipo()));
        validarDatos(dto);

        dto.setLeida(false);
        dto.setFechaCreacion(LocalDateTime.now());

        Notificacion notificacion = repository.save(dto.toModel());
        log.info("Notificacion creada id={}", notificacion.getId());

        return NotificacionDTO.fromModel(notificacion);
    }


    //ACTUALIZAR
    public NotificacionDTO marcarComoLeida(Long id) {
        log.info("Marcando notificacion como leida id={}", id);
        Notificacion notificacion = obtenerNotificacion(id);
        if ( Boolean.TRUE.equals(notificacion.getLeida())){
            log.warn("Intento de leer una notificacion ya leida id={}", id);
            throw new BadRequestException("No se puede leer una notificacion ya leida");
        }

        notificacion.setLeida(true);
        Notificacion leida = repository.save(notificacion);
        log.info("Notificacion marcada como leida id={}", id);

        return NotificacionDTO.fromModel(leida);

    }


    //MARCAR TODAS COMO LEIDAS

    public void marcarTodasComoLeidas(Long usuarioId) {
        log.info("Marcando todas las notificaciones como leidas usuarioId={}", usuarioId);
        validarUsuario(usuarioId);
        List<Notificacion> notificaciones = repository.findByUsuarioIdAndLeidaFalse(usuarioId);
        notificaciones.forEach(notificacion -> notificacion.setLeida(true));
        repository.saveAll(notificaciones);
        log.info("Notificacion marcadas como leidas usuarioId={} total={}", usuarioId, notificaciones.size());
    }



    //ELIMINAR
    public void eliminar(Long id) {
        log.info("Eliminando notificacion id={}", id);
        Notificacion notificacion = obtenerNotificacion(id);
        
        if (Boolean.TRUE.equals(notificacion.getLeida())){
            log.warn("Intento de eliminar notificacion no leida id={}", id);
            throw new BadRequestException("No se puede eliminar una notificacion no leida");
        }

        repository.deleteById(id);

        log.info("Notificacion eliminada id={}", id);

        }
    


    //BUSCAR SOLO LAS NO LEIDAS
    public List<NotificacionDTO> obtenerNoLeidas(){
        log.info("Buscando todasd las notificaciones no leidas");

        List<NotificacionDTO> notificaciones = repository.findByLeidaFalse()
        .stream()
        .map(NotificacionDTO::fromModel)
        .toList();

        log.info("Total de notificaciones no leidas={}", notificaciones.size());
        return notificaciones;
    }


    //METODOS PRIVADOS
    //Confirma existensia de notificaciones y valida datos de notificacion.
    private Notificacion obtenerNotificacion(Long id) {
        if (id == null){
            log.warn("Busqueda de notificacion con id nulo");
            throw new BadRequestException("Busqueda de notificacion con id nulo");
        }

        return repository.findById(id)
        .orElseThrow(() -> {
            log.warn("Notificacion no encontrada id={}", id);
            return new ResourceNotFoundException("Notificacion no encontrada con id" + id);
        });
    }

    private void validarDatos(NotificacionDTO dto) {
        if (dto == null) {
            log.warn("Notificacion vacia");
            throw new BadRequestException("Los datos de la notificacion son obligatorios");
        }
    
        if (dto.getUsuarioId() == null) {
            log.warn("Notificacion sin usuario");
            throw new BadRequestException("El usuario es obligatorio");
        }
    
        if (dto.getTitulo() == null || dto.getTitulo().isBlank()) {
            log.warn("Notificacion sin titulo usuarioId={}", dto.getUsuarioId());
            throw new BadRequestException("El titulo es obligatorio");
        }
    
        if (dto.getMensaje() == null || dto.getMensaje().isBlank()) {
            log.warn("Notificacion sin mensaje usuarioId={}", dto.getUsuarioId());
            throw new BadRequestException("El mensaje es obligatorio");
        }
    
        validarUsuario(dto.getUsuarioId());
        validarTipoPermitido(dto.getTipo());
        validarCanalPermitido(dto.getCanal());
    }

    private void validarUsuario(Long usuarioId){
        if (usuarioId == null){
            log.warn("Usuario nulo");
            throw new BadRequestException("El usuario es obligatorio");
        }

        if (!usuarioClient.usuarioExiste(usuarioId)){
            log.warn("Usuario no existe usuarioId={}", usuarioId);
            throw new BadRequestException("El usuario no existe");
        }
    }

    private String normalizarTipo(String tipo){
        if (tipo == null){
            log.warn("Tipo nulo");
            throw new BadRequestException("El tipo es obligatorio");
        }

        return tipo.trim().toUpperCase();
    }

    private String normalizarCanal(String canal){
        if (canal == null){
            log.warn("Canal nulo");
            throw new BadRequestException("El canal es obligatorio");
        }

        return canal.trim().toUpperCase();
    }

    private void validarTipoPermitido(String tipo) {
        List<String> tiposPermitidos = List.of(
                "RESERVA",
                "PAGO",
                "MANTENIMIENTO",
                "TORNEO",
                "PROMOCION",
                "SISTEMA"
        );
    
        if (!tiposPermitidos.contains(tipo)) {
            log.warn("Tipo de notificacion invalido tipo={}", tipo);
            throw new BadRequestException("Tipo no valido. Ingrese uno de los siguientes: RESERVA, PAGO, MANTENIMIENTO, TORNEO, PROMOCION, SISTEMA");
        }
    }

    private void validarCanalPermitido(String canal) {
        List<String> canalesPermitidos = List.of(
                "SISTEMA",
                "EMAIL",
                "SMS_SIMULADO",
                "WHATSAPP_SIMULADO"
        );
    
        if (!canalesPermitidos.contains(canal)) {
            log.warn("Canal de notificacion invalido canal={}", canal);
            throw new BadRequestException("Canal no valido. Ingrese uno de los siguientes: SISTEMA, EMAIL, SMS_SIMULADO, WHATSAPP_SIMULADO");
        }
    }

}
