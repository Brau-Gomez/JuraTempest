package com.juratempest.ms_mantenimiento.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.juratempest.ms_mantenimiento.client.MaquinaClient;
import com.juratempest.ms_mantenimiento.client.NotificacionClient;
import com.juratempest.ms_mantenimiento.client.UsuarioClient;
import com.juratempest.ms_mantenimiento.dto.CrearNotificacionRequestDTO;
import com.juratempest.ms_mantenimiento.dto.MantenimientoDTO;
import com.juratempest.ms_mantenimiento.exception.BadRequestException;
import com.juratempest.ms_mantenimiento.exception.ResourceNotFoundException;
import com.juratempest.ms_mantenimiento.model.Mantenimiento;
import com.juratempest.ms_mantenimiento.repository.MantenimientoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MantenimientoService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    private static final String ESTADO_FINALIZADO = "FINALIZADO";
    private static final String ESTADO_CANCELADO = "CANCELADO";

    private final MantenimientoRepository repository;
    private final MaquinaClient maquinaClient;
    private final UsuarioClient usuarioClient;
    private final NotificacionClient notificacionClient;

    public MantenimientoService(MantenimientoRepository repository, MaquinaClient maquinaClient,
            UsuarioClient usuarioClient, NotificacionClient notificacionClient) {
        this.repository = repository;
        this.maquinaClient = maquinaClient;
        this.usuarioClient = usuarioClient;
        this.notificacionClient = notificacionClient;
    }

    public List<MantenimientoDTO> listar() {
        log.info("Listando mantenimientos");
        List<MantenimientoDTO> mantenimientos = repository.findAll().stream()
                .map(MantenimientoDTO::fromModel)
                .toList();
        log.info("Total de mantenimientos encontrados={}", mantenimientos.size());
        return mantenimientos;
    }

    public MantenimientoDTO buscarPorId(Long id) {
        log.info("Buscando mantenimiento id={}", id);
        return MantenimientoDTO.fromModel(obtenerMantenimiento(id));
    }

    public List<MantenimientoDTO> buscarPorMaquina(Long maquinaId) {
        log.info("Buscando mantenimientos por maquinaId={}", maquinaId);
        validarMaquina(maquinaId);
        return repository.findByMaquinaId(maquinaId).stream()
                .map(MantenimientoDTO::fromModel)
                .toList();
    }

    public List<MantenimientoDTO> buscarPorEstado(String estado) {
        String estadoNormalizado = normalizarEstado(estado);
        validarEstadoPermitido(estadoNormalizado);
        log.info("Buscando mantenimientos por estado={}", estadoNormalizado);
        return repository.findByEstado(estadoNormalizado).stream()
                .map(MantenimientoDTO::fromModel)
                .toList();
    }

    public List<MantenimientoDTO> buscarPorTipo(String tipo) {
        String tipoNormalizado = normalizarTipo(tipo);
        validarTipoPermitido(tipoNormalizado);
        log.info("Buscando mantenimientos por tipo={}", tipoNormalizado);
        return repository.findByTipo(tipoNormalizado).stream()
                .map(MantenimientoDTO::fromModel)
                .toList();
    }

    public MantenimientoDTO crear(MantenimientoDTO dto) {
        log.info("Creando mantenimiento maquinaId={}", dto != null ? dto.getMaquinaId() : null);
        if (dto == null) {
            throw new BadRequestException("El cuerpo del mantenimiento es obligatorio");
        }

        dto.setTipo(normalizarTipo(dto.getTipo()));
        dto.setEstado(ESTADO_PENDIENTE);
        if (dto.getFechaInicio() == null) {
            dto.setFechaInicio(LocalDate.now());
        }

        validarDatos(dto);
        Mantenimiento guardado = repository.save(dto.toModel());
        notificar(guardado, "Mantenimiento creado", "Se creo un mantenimiento para la maquina " + guardado.getMaquinaId());
        log.info("Mantenimiento creado id={}", guardado.getId());
        return MantenimientoDTO.fromModel(guardado);
    }

    public MantenimientoDTO actualizar(Long id, MantenimientoDTO dto) {
        log.info("Actualizando mantenimiento id={}", id);
        if (dto == null) {
            throw new BadRequestException("El cuerpo del mantenimiento es obligatorio");
        }

        Mantenimiento existente = obtenerMantenimiento(id);
        dto.setTipo(normalizarTipo(dto.getTipo()));
        dto.setEstado(existente.getEstado());
        if (dto.getFechaInicio() == null) {
            dto.setFechaInicio(existente.getFechaInicio());
        }

        validarDatos(dto);
        existente.setMaquinaId(dto.getMaquinaId());
        existente.setUsuarioOperadorId(dto.getUsuarioOperadorId());
        existente.setTipo(dto.getTipo());
        existente.setDescripcion(dto.getDescripcion());
        existente.setTecnico(dto.getTecnico());
        existente.setFechaInicio(dto.getFechaInicio());
        existente.setFechaFin(dto.getFechaFin());
        existente.setCosto(dto.getCosto());

        Mantenimiento actualizado = repository.save(existente);
        log.info("Mantenimiento actualizado id={}", actualizado.getId());
        return MantenimientoDTO.fromModel(actualizado);
    }

    public MantenimientoDTO iniciar(Long id) {
        log.info("Iniciando mantenimiento id={}", id);
        Mantenimiento mantenimiento = obtenerMantenimiento(id);
        if (!ESTADO_PENDIENTE.equals(mantenimiento.getEstado())) {
            throw new BadRequestException("Solo se puede iniciar un mantenimiento pendiente");
        }

        mantenimiento.setEstado(ESTADO_EN_PROCESO);
        Mantenimiento actualizado = repository.save(mantenimiento);
        notificar(actualizado, "Mantenimiento iniciado", "Se inicio el mantenimiento de la maquina " + actualizado.getMaquinaId());
        return MantenimientoDTO.fromModel(actualizado);
    }

    public MantenimientoDTO cerrar(Long id) {
        log.info("Cerrando mantenimiento id={}", id);
        Mantenimiento mantenimiento = obtenerMantenimiento(id);
        if (!ESTADO_EN_PROCESO.equals(mantenimiento.getEstado())) {
            throw new BadRequestException("Solo se puede cerrar un mantenimiento en proceso");
        }

        mantenimiento.setEstado(ESTADO_FINALIZADO);
        if (mantenimiento.getFechaFin() == null) {
            mantenimiento.setFechaFin(LocalDate.now());
        }

        Mantenimiento actualizado = repository.save(mantenimiento);
        notificar(actualizado, "Mantenimiento finalizado", "Se finalizo el mantenimiento de la maquina " + actualizado.getMaquinaId());
        return MantenimientoDTO.fromModel(actualizado);
    }

    public MantenimientoDTO cancelar(Long id) {
        log.info("Cancelando mantenimiento id={}", id);
        Mantenimiento mantenimiento = obtenerMantenimiento(id);
        if (ESTADO_FINALIZADO.equals(mantenimiento.getEstado())) {
            throw new BadRequestException("No se puede cancelar un mantenimiento finalizado");
        }

        mantenimiento.setEstado(ESTADO_CANCELADO);
        if (mantenimiento.getFechaFin() == null) {
            mantenimiento.setFechaFin(LocalDate.now());
        }

        Mantenimiento actualizado = repository.save(mantenimiento);
        notificar(actualizado, "Mantenimiento cancelado", "Se cancelo el mantenimiento de la maquina " + actualizado.getMaquinaId());
        return MantenimientoDTO.fromModel(actualizado);
    }

    public void eliminar(Long id) {
        log.info("Eliminando mantenimiento id={}", id);
        Mantenimiento mantenimiento = obtenerMantenimiento(id);
        if (ESTADO_EN_PROCESO.equals(mantenimiento.getEstado()) || ESTADO_FINALIZADO.equals(mantenimiento.getEstado())) {
            throw new BadRequestException("No se puede eliminar un mantenimiento en proceso o finalizado");
        }

        repository.deleteById(id);
        log.info("Mantenimiento eliminado id={}", id);
    }

    private Mantenimiento obtenerMantenimiento(Long id) {
        if (id == null) {
            throw new BadRequestException("El id del mantenimiento es obligatorio");
        }

        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mantenimiento no encontrado con id " + id));
    }

    private void validarDatos(MantenimientoDTO dto) {
        if (dto.getMaquinaId() == null) {
            throw new BadRequestException("La maquina es obligatoria");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripcion es obligatoria");
        }
        if (dto.getTecnico() == null || dto.getTecnico().isBlank()) {
            throw new BadRequestException("El tecnico es obligatorio");
        }
        if (dto.getCosto() != null && dto.getCosto() < 0) {
            throw new BadRequestException("El costo no puede ser negativo");
        }
        if (dto.getFechaFin() != null && dto.getFechaInicio() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            throw new BadRequestException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        validarTipoPermitido(dto.getTipo());
        validarEstadoPermitido(dto.getEstado());
        validarMaquina(dto.getMaquinaId());
        validarUsuarioOperador(dto.getUsuarioOperadorId());
    }

    private void validarMaquina(Long maquinaId) {
        if (!maquinaClient.existe(maquinaId)) {
            throw new BadRequestException("La maquina no existe");
        }
    }

    private void validarUsuarioOperador(Long usuarioOperadorId) {
        if (usuarioOperadorId != null && !usuarioClient.usuarioExiste(usuarioOperadorId)) {
            throw new BadRequestException("El usuario operador no existe");
        }
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new BadRequestException("El tipo es obligatorio");
        }
        return tipo.trim().toUpperCase();
    }

    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BadRequestException("El estado es obligatorio");
        }
        return estado.trim().toUpperCase();
    }

    private void validarTipoPermitido(String tipo) {
        List<String> tiposPermitidos = List.of("PREVENTIVO", "CORRECTIVO", "FALLA_REPORTADA");
        if (!tiposPermitidos.contains(tipo)) {
            throw new BadRequestException("Tipo no valido. Ingrese PREVENTIVO, CORRECTIVO o FALLA_REPORTADA");
        }
    }

    private void validarEstadoPermitido(String estado) {
        List<String> estadosPermitidos = List.of(ESTADO_PENDIENTE, ESTADO_EN_PROCESO, ESTADO_FINALIZADO, ESTADO_CANCELADO);
        if (!estadosPermitidos.contains(estado)) {
            throw new BadRequestException("Estado no valido. Ingrese PENDIENTE, EN_PROCESO, FINALIZADO o CANCELADO");
        }
    }

    private void notificar(Mantenimiento mantenimiento, String titulo, String mensaje) {
        notificacionClient.crearNotificacion(CrearNotificacionRequestDTO.builder()
                .usuarioId(mantenimiento.getUsuarioOperadorId())
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo("MANTENIMIENTO")
                .canal("SISTEMA")
                .build());
    }
}
