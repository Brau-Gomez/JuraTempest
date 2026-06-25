package com.juratempest.ms_eventos_torneos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juratempest.ms_eventos_torneos.client.FidelizacionClient;
import com.juratempest.ms_eventos_torneos.client.HorarioClient;
import com.juratempest.ms_eventos_torneos.client.MaquinaClient;
import com.juratempest.ms_eventos_torneos.client.NotificacionClient;
import com.juratempest.ms_eventos_torneos.client.UsuarioClient;
import com.juratempest.ms_eventos_torneos.dto.CrearFidelizacionRequestDTO;
import com.juratempest.ms_eventos_torneos.dto.CrearNotificacionRequestDTO;
import com.juratempest.ms_eventos_torneos.dto.InscripcionTorneoDTO;
import com.juratempest.ms_eventos_torneos.dto.TorneoDTO;
import com.juratempest.ms_eventos_torneos.exception.BadRequestException;
import com.juratempest.ms_eventos_torneos.exception.ResourceNotFoundException;
import com.juratempest.ms_eventos_torneos.model.InscripcionTorneo;
import com.juratempest.ms_eventos_torneos.model.Torneo;
import com.juratempest.ms_eventos_torneos.repository.InscripcionTorneoRepository;
import com.juratempest.ms_eventos_torneos.repository.TorneoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TorneoService {

    private static final String TORNEO_PROGRAMADO = "PROGRAMADO";
    private static final String TORNEO_ABIERTO = "ABIERTO";
    private static final String TORNEO_CERRADO = "CERRADO";
    private static final String TORNEO_FINALIZADO = "FINALIZADO";
    private static final String TORNEO_CANCELADO = "CANCELADO";
    private static final String INSCRIPCION_INSCRITO = "INSCRITO";
    private static final String INSCRIPCION_CANCELADO = "CANCELADO";
    private static final int PUNTOS_GANADOR = 100;

    private final TorneoRepository torneoRepository;
    private final InscripcionTorneoRepository inscripcionRepository;
    private final UsuarioClient usuarioClient;
    private final MaquinaClient maquinaClient;
    private final HorarioClient horarioClient;
    private final FidelizacionClient fidelizacionClient;
    private final NotificacionClient notificacionClient;

    public TorneoService(TorneoRepository torneoRepository, InscripcionTorneoRepository inscripcionRepository,
            UsuarioClient usuarioClient, MaquinaClient maquinaClient, HorarioClient horarioClient,
            FidelizacionClient fidelizacionClient, NotificacionClient notificacionClient) {
        this.torneoRepository = torneoRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.usuarioClient = usuarioClient;
        this.maquinaClient = maquinaClient;
        this.horarioClient = horarioClient;
        this.fidelizacionClient = fidelizacionClient;
        this.notificacionClient = notificacionClient;
    }

    public List<TorneoDTO> listar() {
        log.info("Listando torneos");
        List<TorneoDTO> torneos = torneoRepository.findAll().stream()
                .map(TorneoDTO::fromModel)
                .toList();
        log.info("Total de torneos encontrados={}", torneos.size());
        return torneos;
    }

    public TorneoDTO buscarPorId(Long id) {
        log.info("Buscando torneo id={}", id);
        return TorneoDTO.fromModel(obtenerTorneo(id));
    }

    public List<TorneoDTO> listarDisponibles() {
        log.info("Listando torneos disponibles");
        return torneoRepository.findByEstadoAndCuposDisponiblesGreaterThan(TORNEO_ABIERTO, 0).stream()
                .map(TorneoDTO::fromModel)
                .toList();
    }

    public List<TorneoDTO> buscarPorEstado(String estado) {
        String estadoNormalizado = normalizarEstadoTorneo(estado);
        validarEstadoTorneoPermitido(estadoNormalizado);
        log.info("Buscando torneos por estado={}", estadoNormalizado);
        return torneoRepository.findByEstado(estadoNormalizado).stream()
                .map(TorneoDTO::fromModel)
                .toList();
    }

    public List<InscripcionTorneoDTO> listarInscritos(Long torneoId) {
        log.info("Listando inscritos torneoId={}", torneoId);
        obtenerTorneo(torneoId);
        return inscripcionRepository.findByTorneoIdAndEstado(torneoId, INSCRIPCION_INSCRITO).stream()
                .map(InscripcionTorneoDTO::fromModel)
                .toList();
    }

    public List<InscripcionTorneoDTO> listarInscripcionesPorUsuario(Long usuarioId) {
        log.info("Listando inscripciones usuarioId={}", usuarioId);
        validarUsuario(usuarioId);
        return inscripcionRepository.findByUsuarioId(usuarioId).stream()
                .map(InscripcionTorneoDTO::fromModel)
                .toList();
    }

    @Transactional
    public TorneoDTO crear(TorneoDTO dto) {
        log.info("Creando torneo maquinaId={} horarioId={}", dto != null ? dto.getMaquinaId() : null,
                dto != null ? dto.getHorarioId() : null);
        if (dto == null) {
            throw new BadRequestException("El cuerpo del torneo es obligatorio");
        }

        dto.setNombre(normalizarTexto(dto.getNombre(), "El nombre es obligatorio"));
        dto.setDescripcion(normalizarTexto(dto.getDescripcion(), "La descripcion es obligatoria"));
        dto.setEstado(TORNEO_PROGRAMADO);
        dto.setCuposDisponibles(dto.getCuposMaximos());
        dto.setFechaCreacion(LocalDate.now());
        dto.setGanadorUsuarioId(null);

        validarDatosTorneo(dto);
        Torneo guardado = torneoRepository.save(dto.toModel());
        log.info("Torneo creado id={}", guardado.getId());
        return TorneoDTO.fromModel(guardado);
    }

    @Transactional
    public TorneoDTO actualizar(Long id, TorneoDTO dto) {
        log.info("Actualizando torneo id={}", id);
        if (dto == null) {
            throw new BadRequestException("El cuerpo del torneo es obligatorio");
        }

        Torneo existente = obtenerTorneo(id);
        int cuposOcupados = existente.getCuposMaximos() - existente.getCuposDisponibles();
        if (dto.getCuposMaximos() == null || dto.getCuposMaximos() <= 0) {
            throw new BadRequestException("Los cupos maximos deben ser mayores a 0");
        }
        if (dto.getCuposMaximos() < cuposOcupados) {
            throw new BadRequestException("Los cupos maximos no pueden ser menores a las inscripciones activas");
        }

        dto.setNombre(normalizarTexto(dto.getNombre(), "El nombre es obligatorio"));
        dto.setDescripcion(normalizarTexto(dto.getDescripcion(), "La descripcion es obligatoria"));
        dto.setEstado(existente.getEstado());
        dto.setCuposDisponibles(dto.getCuposMaximos() - cuposOcupados);
        dto.setFechaCreacion(existente.getFechaCreacion());
        dto.setGanadorUsuarioId(existente.getGanadorUsuarioId());
        validarDatosTorneo(dto);

        existente.setNombre(dto.getNombre());
        existente.setDescripcion(dto.getDescripcion());
        existente.setMaquinaId(dto.getMaquinaId());
        existente.setHorarioId(dto.getHorarioId());
        existente.setCuposMaximos(dto.getCuposMaximos());
        existente.setCuposDisponibles(dto.getCuposDisponibles());
        Torneo actualizado = torneoRepository.save(existente);
        log.info("Torneo actualizado id={}", actualizado.getId());
        return TorneoDTO.fromModel(actualizado);
    }

    @Transactional
    public TorneoDTO abrirInscripciones(Long id) {
        log.info("Abriendo inscripciones torneoId={}", id);
        Torneo torneo = obtenerTorneo(id);
        if (TORNEO_FINALIZADO.equals(torneo.getEstado()) || TORNEO_CANCELADO.equals(torneo.getEstado())) {
            throw new BadRequestException("No se puede abrir un torneo finalizado o cancelado");
        }
        torneo.setEstado(TORNEO_ABIERTO);
        return TorneoDTO.fromModel(torneoRepository.save(torneo));
    }

    @Transactional
    public TorneoDTO cerrarInscripciones(Long id) {
        log.info("Cerrando inscripciones torneoId={}", id);
        Torneo torneo = obtenerTorneo(id);
        if (TORNEO_FINALIZADO.equals(torneo.getEstado()) || TORNEO_CANCELADO.equals(torneo.getEstado())) {
            throw new BadRequestException("No se puede cerrar un torneo finalizado o cancelado");
        }
        torneo.setEstado(TORNEO_CERRADO);
        return TorneoDTO.fromModel(torneoRepository.save(torneo));
    }

    @Transactional
    public InscripcionTorneoDTO inscribirUsuario(Long torneoId, Long usuarioId) {
        log.info("Inscribiendo usuario torneoId={} usuarioId={}", torneoId, usuarioId);
        Torneo torneo = obtenerTorneo(torneoId);
        validarUsuario(usuarioId);
        if (!TORNEO_ABIERTO.equals(torneo.getEstado())) {
            throw new BadRequestException("Solo se puede inscribir en torneos abiertos");
        }
        if (torneo.getCuposDisponibles() <= 0) {
            throw new BadRequestException("No hay cupos disponibles");
        }
        if (inscripcionRepository.findByTorneoIdAndUsuarioIdAndEstado(torneoId, usuarioId, INSCRIPCION_INSCRITO).isPresent()) {
            throw new BadRequestException("El usuario ya tiene una inscripcion activa en este torneo");
        }

        InscripcionTorneo inscripcion = InscripcionTorneo.builder()
                .torneoId(torneoId)
                .usuarioId(usuarioId)
                .estado(INSCRIPCION_INSCRITO)
                .fechaInscripcion(LocalDateTime.now())
                .build();

        torneo.setCuposDisponibles(torneo.getCuposDisponibles() - 1);
        torneoRepository.save(torneo);
        InscripcionTorneo guardada = inscripcionRepository.save(inscripcion);
        notificar(usuarioId, "Inscripcion a torneo", "Tu inscripcion al torneo " + torneo.getNombre() + " fue registrada");
        log.info("Inscripcion creada id={} torneoId={} usuarioId={}", guardada.getId(), torneoId, usuarioId);
        return InscripcionTorneoDTO.fromModel(guardada);
    }

    @Transactional
    public InscripcionTorneoDTO cancelarInscripcion(Long torneoId, Long usuarioId) {
        log.info("Cancelando inscripcion torneoId={} usuarioId={}", torneoId, usuarioId);
        Torneo torneo = obtenerTorneo(torneoId);
        InscripcionTorneo inscripcion = inscripcionRepository
                .findByTorneoIdAndUsuarioIdAndEstado(torneoId, usuarioId, INSCRIPCION_INSCRITO)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripcion activa no encontrada"));

        inscripcion.setEstado(INSCRIPCION_CANCELADO);
        if (torneo.getCuposDisponibles() < torneo.getCuposMaximos()) {
            torneo.setCuposDisponibles(torneo.getCuposDisponibles() + 1);
        }
        torneoRepository.save(torneo);
        InscripcionTorneo actualizada = inscripcionRepository.save(inscripcion);
        notificar(usuarioId, "Inscripcion cancelada", "Tu inscripcion al torneo " + torneo.getNombre() + " fue cancelada");
        return InscripcionTorneoDTO.fromModel(actualizada);
    }

    @Transactional
    public TorneoDTO finalizar(Long id, Long ganadorUsuarioId) {
        log.info("Finalizando torneo id={} ganadorUsuarioId={}", id, ganadorUsuarioId);
        Torneo torneo = obtenerTorneo(id);
        validarUsuario(ganadorUsuarioId);
        if (TORNEO_FINALIZADO.equals(torneo.getEstado()) || TORNEO_CANCELADO.equals(torneo.getEstado())) {
            throw new BadRequestException("No se puede finalizar un torneo finalizado o cancelado");
        }
        if (inscripcionRepository.findByTorneoIdAndUsuarioIdAndEstado(id, ganadorUsuarioId, INSCRIPCION_INSCRITO).isEmpty()) {
            throw new BadRequestException("El ganador debe tener una inscripcion activa en el torneo");
        }

        torneo.setEstado(TORNEO_FINALIZADO);
        torneo.setGanadorUsuarioId(ganadorUsuarioId);
        Torneo actualizado = torneoRepository.save(torneo);
        fidelizacionClient.registrarPuntos(CrearFidelizacionRequestDTO.builder()
                .usuarioId(ganadorUsuarioId)
                .puntos(PUNTOS_GANADOR)
                .descripcion("Premio ganador torneo " + torneo.getNombre())
                .build());
        notificar(ganadorUsuarioId, "Torneo finalizado", "Ganaste el torneo " + torneo.getNombre());
        return TorneoDTO.fromModel(actualizado);
    }

    @Transactional
    public TorneoDTO cancelarTorneo(Long id) {
        log.info("Cancelando torneo id={}", id);
        Torneo torneo = obtenerTorneo(id);
        if (TORNEO_FINALIZADO.equals(torneo.getEstado())) {
            throw new BadRequestException("No se puede cancelar un torneo finalizado");
        }
        torneo.setEstado(TORNEO_CANCELADO);
        return TorneoDTO.fromModel(torneoRepository.save(torneo));
    }

    @Transactional
    public void eliminar(Long id) {
        log.info("Eliminando torneo id={}", id);
        Torneo torneo = obtenerTorneo(id);
        if (inscripcionRepository.existsByTorneoIdAndEstado(torneo.getId(), INSCRIPCION_INSCRITO)) {
            throw new BadRequestException("No se puede eliminar un torneo con inscripciones activas");
        }
        inscripcionRepository.deleteAll(inscripcionRepository.findByTorneoId(torneo.getId()));
        torneoRepository.deleteById(id);
        log.info("Torneo eliminado id={}", id);
    }

    private Torneo obtenerTorneo(Long id) {
        if (id == null) {
            throw new BadRequestException("El id del torneo es obligatorio");
        }
        return torneoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Torneo no encontrado con id " + id));
    }

    private void validarDatosTorneo(TorneoDTO dto) {
        if (dto.getCuposMaximos() == null || dto.getCuposMaximos() <= 0) {
            throw new BadRequestException("Los cupos maximos deben ser mayores a 0");
        }
        if (dto.getCuposDisponibles() == null || dto.getCuposDisponibles() < 0) {
            throw new BadRequestException("Los cupos disponibles no pueden ser negativos");
        }
        if (dto.getCuposDisponibles() > dto.getCuposMaximos()) {
            throw new BadRequestException("Los cupos disponibles no pueden superar los cupos maximos");
        }
        validarEstadoTorneoPermitido(dto.getEstado());
        validarMaquina(dto.getMaquinaId());
        validarHorario(dto.getHorarioId());
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioClient.usuarioExiste(usuarioId)) {
            throw new BadRequestException("El usuario no existe");
        }
    }

    private void validarMaquina(Long maquinaId) {
        if (!maquinaClient.existe(maquinaId)) {
            throw new BadRequestException("La maquina no existe");
        }
        if (!maquinaClient.estaActiva(maquinaId)) {
            throw new BadRequestException("La maquina no esta activa");
        }
    }

    private void validarHorario(Long horarioId) {
        if (!horarioClient.existe(horarioId)) {
            throw new BadRequestException("El horario no existe");
        }
    }

    private String normalizarEstadoTorneo(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new BadRequestException("El estado es obligatorio");
        }
        return estado.trim().toUpperCase();
    }

    private String normalizarTexto(String valor, String mensaje) {
        if (valor == null || valor.isBlank()) {
            throw new BadRequestException(mensaje);
        }
        return valor.trim();
    }

    private void validarEstadoTorneoPermitido(String estado) {
        List<String> estados = List.of(TORNEO_PROGRAMADO, TORNEO_ABIERTO, TORNEO_CERRADO, TORNEO_FINALIZADO, TORNEO_CANCELADO);
        if (!estados.contains(estado)) {
            throw new BadRequestException("Estado no valido. Ingrese PROGRAMADO, ABIERTO, CERRADO, FINALIZADO o CANCELADO");
        }
    }

    private void notificar(Long usuarioId, String titulo, String mensaje) {
        notificacionClient.crearNotificacion(CrearNotificacionRequestDTO.builder()
                .usuarioId(usuarioId)
                .titulo(titulo)
                .mensaje(mensaje)
                .tipo("TORNEO")
                .canal("SISTEMA")
                .build());
    }
}
