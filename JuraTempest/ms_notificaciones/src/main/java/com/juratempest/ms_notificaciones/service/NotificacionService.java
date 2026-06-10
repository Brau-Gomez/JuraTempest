package com.juratempest.ms_notificaciones.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.model.Notificacion;
import com.juratempest.ms_notificaciones.repository.NotificacionRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        // TODO: Asignar el repository al atributo de la clase para poder consultar y persistir notificaciones.
        this.repository = repository;
    }

    public List<NotificacionDTO> listar() {
        // TODO: Buscar todas las notificaciones con repository.findAll().
        // TODO: Convertir cada entidad Notificacion a NotificacionDTO usando NotificacionDTO.fromModel.
        // TODO: Retornar la lista de DTOs para que el controlador responda al cliente.
        return List.of();
    }

    public NotificacionDTO buscarPorId(Long id) {
        // TODO: Obtener la entidad usando obtenerNotificacion(id).
        // TODO: Convertir la entidad encontrada a DTO.
        // TODO: Retornar el DTO o lanzar ResourceNotFoundException desde obtenerNotificacion si no existe.
        return null;
    }

    public List<NotificacionDTO> buscarPorUsuario(Long usuarioId) {
        // TODO: Validar que usuarioId no sea null.
        // TODO: Buscar notificaciones por usuarioId usando repository.findByUsuarioId(usuarioId).
        // TODO: Convertir el resultado a una lista de DTOs.
        return List.of();
    }

    public List<NotificacionDTO> buscarNoLeidasPorUsuario(Long usuarioId) {
        // TODO: Validar que usuarioId no sea null.
        // TODO: Buscar notificaciones no leidas usando repository.findByUsuarioIdAndLeidaFalse(usuarioId).
        // TODO: Convertir el resultado a una lista de DTOs.
        return List.of();
    }

    public List<NotificacionDTO> buscarPorTipo(String tipo) {
        // TODO: Validar que tipo no venga null ni vacio.
        // TODO: Normalizar tipo a mayusculas.
        // TODO: Validar que tipo sea uno de: RESERVA, PAGO, MANTENIMIENTO, TORNEO, PROMOCION, SISTEMA.
        // TODO: Buscar notificaciones por tipo usando repository.findByTipo(tipoNormalizado).
        // TODO: Convertir el resultado a DTOs.
        return List.of();
    }

    public Long totalNoLeidasPorUsuario(Long usuarioId) {
        // TODO: Validar que usuarioId no sea null.
        // TODO: Contar notificaciones no leidas con repository.countByUsuarioIdAndLeidaFalse(usuarioId).
        // TODO: Retornar el total encontrado.
        return 0L;
    }

    public NotificacionDTO crear(NotificacionDTO dto) {
        // TODO: Validar los datos principales del DTO con validarDatos(dto).
        // TODO: Convertir el DTO a entidad Notificacion.
        // TODO: Asignar leida = false para toda notificacion nueva.
        // TODO: Asignar fechaCreacion = LocalDateTime.now().
        // TODO: Normalizar tipo y canal a mayusculas antes de guardar.
        // TODO: Guardar la entidad con repository.save().
        // TODO: Convertir la entidad guardada a DTO y retornarla.
        return null;
    }

    public NotificacionDTO marcarComoLeida(Long id) {
        // TODO: Buscar la notificacion por id usando obtenerNotificacion(id).
        // TODO: Cambiar leida a true.
        // TODO: Guardar los cambios con repository.save().
        // TODO: Retornar la notificacion actualizada como DTO.
        return null;
    }

    public void marcarTodasComoLeidas(Long usuarioId) {
        // TODO: Validar que usuarioId no sea null.
        // TODO: Buscar todas las notificaciones no leidas del usuario.
        // TODO: Recorrer la lista y cambiar leida a true en cada entidad.
        // TODO: Guardar todas las notificaciones actualizadas con repository.saveAll().
    }

    public void eliminar(Long id) {
        // TODO: Validar que la notificacion exista usando obtenerNotificacion(id).
        // TODO: Eliminar la notificacion con repository.deleteById(id).
        // TODO: Registrar en logs que la eliminacion fue ejecutada.
    }

    private Notificacion obtenerNotificacion(Long id) {
        // TODO: Validar que id no sea null.
        // TODO: Buscar la notificacion por id usando repository.findById(id).
        // TODO: Si no existe, lanzar ResourceNotFoundException con un mensaje claro.
        // TODO: Retornar la entidad encontrada.
        return null;
    }

    private void validarDatos(NotificacionDTO dto) {
        // TODO: Validar que dto no sea null.
        // TODO: Validar que usuarioId no sea null.
        // TODO: Validar que titulo no venga null ni vacio.
        // TODO: Validar que mensaje no venga null ni vacio.
        // TODO: Validar que tipo sea uno de: RESERVA, PAGO, MANTENIMIENTO, TORNEO, PROMOCION, SISTEMA.
        // TODO: Validar que canal sea uno de: SISTEMA, EMAIL, SMS_SIMULADO, WHATSAPP_SIMULADO.
        log.debug("Metodo validarDatos pendiente de implementar");
    }
}
