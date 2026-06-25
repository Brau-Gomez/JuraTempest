package com.juratempest.ms_eventos_torneos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.juratempest.ms_eventos_torneos.client.FidelizacionClient;
import com.juratempest.ms_eventos_torneos.client.HorarioClient;
import com.juratempest.ms_eventos_torneos.client.MaquinaClient;
import com.juratempest.ms_eventos_torneos.client.NotificacionClient;
import com.juratempest.ms_eventos_torneos.client.UsuarioClient;
import com.juratempest.ms_eventos_torneos.dto.InscripcionTorneoDTO;
import com.juratempest.ms_eventos_torneos.dto.TorneoDTO;
import com.juratempest.ms_eventos_torneos.exception.BadRequestException;
import com.juratempest.ms_eventos_torneos.model.InscripcionTorneo;
import com.juratempest.ms_eventos_torneos.model.Torneo;
import com.juratempest.ms_eventos_torneos.repository.InscripcionTorneoRepository;
import com.juratempest.ms_eventos_torneos.repository.TorneoRepository;

@ExtendWith(MockitoExtension.class)
class TorneoServiceTest {

    @Mock
    private TorneoRepository torneoRepository;
    @Mock
    private InscripcionTorneoRepository inscripcionRepository;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private MaquinaClient maquinaClient;
    @Mock
    private HorarioClient horarioClient;
    @Mock
    private FidelizacionClient fidelizacionClient;
    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private TorneoService service;

    @Test
    void testListar() {
        // Given
        when(torneoRepository.findAll()).thenReturn(List.of(torneoProgramado()));

        // When
        List<TorneoDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("PROGRAMADO", resultado.get(0).getEstado());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoProgramado()));

        // When
        TorneoDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
        assertEquals("Copa Arcade", resultado.getNombre());
    }

    @Test
    void testCrear() {
        // Given
        when(maquinaClient.existe(1L)).thenReturn(true);
        when(maquinaClient.estaActiva(1L)).thenReturn(true);
        when(horarioClient.existe(1L)).thenReturn(true);
        when(torneoRepository.save(any(Torneo.class))).thenReturn(torneoProgramado());

        // When
        TorneoDTO resultado = service.crear(requestValido());

        // Then
        assertEquals("PROGRAMADO", resultado.getEstado());
        assertEquals(8, resultado.getCuposDisponibles());
    }

    @Test
    void testAbrir() {
        // Given
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoProgramado()));
        when(torneoRepository.save(any(Torneo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TorneoDTO resultado = service.abrirInscripciones(1L);

        // Then
        assertEquals("ABIERTO", resultado.getEstado());
    }

    @Test
    void testInscribirUsuario() {
        // Given
        Torneo torneo = torneoAbierto();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(usuarioClient.usuarioExiste(2L)).thenReturn(true);
        when(inscripcionRepository.findByTorneoIdAndUsuarioIdAndEstado(1L, 2L, "INSCRITO")).thenReturn(Optional.empty());
        when(torneoRepository.save(any(Torneo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inscripcionRepository.save(any(InscripcionTorneo.class))).thenReturn(inscripcionActiva());

        // When
        InscripcionTorneoDTO resultado = service.inscribirUsuario(1L, 2L);

        // Then
        assertEquals("INSCRITO", resultado.getEstado());
        assertEquals(7, torneo.getCuposDisponibles());
    }

    @Test
    void testCancelarInscripcion() {
        // Given
        Torneo torneo = torneoAbierto();
        torneo.setCuposDisponibles(7);
        InscripcionTorneo inscripcion = inscripcionActiva();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(inscripcionRepository.findByTorneoIdAndUsuarioIdAndEstado(1L, 2L, "INSCRITO")).thenReturn(Optional.of(inscripcion));
        when(torneoRepository.save(any(Torneo.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(inscripcionRepository.save(any(InscripcionTorneo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        InscripcionTorneoDTO resultado = service.cancelarInscripcion(1L, 2L);

        // Then
        assertEquals("CANCELADO", resultado.getEstado());
        assertEquals(8, torneo.getCuposDisponibles());
    }

    @Test
    void testFinalizar() {
        // Given
        Torneo torneo = torneoAbierto();
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(usuarioClient.usuarioExiste(2L)).thenReturn(true);
        when(inscripcionRepository.findByTorneoIdAndUsuarioIdAndEstado(1L, 2L, "INSCRITO")).thenReturn(Optional.of(inscripcionActiva()));
        when(torneoRepository.save(any(Torneo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        TorneoDTO resultado = service.finalizar(1L, 2L);

        // Then
        assertEquals("FINALIZADO", resultado.getEstado());
        assertEquals(2L, resultado.getGanadorUsuarioId());
        verify(fidelizacionClient).registrarPuntos(any());
    }

    @Test
    void testEliminarSinInscripcionesActivas() {
        // Given
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneoProgramado()));
        when(inscripcionRepository.existsByTorneoIdAndEstado(1L, "INSCRITO")).thenReturn(false);
        when(inscripcionRepository.findByTorneoId(1L)).thenReturn(List.of());

        // When
        service.eliminar(1L);

        // Then
        verify(torneoRepository).deleteById(1L);
    }

    @Test
    void testInscribirSinCuposLanzaError() {
        // Given
        Torneo torneo = torneoAbierto();
        torneo.setCuposDisponibles(0);
        when(torneoRepository.findById(1L)).thenReturn(Optional.of(torneo));
        when(usuarioClient.usuarioExiste(2L)).thenReturn(true);

        // When / Then
        assertThrows(BadRequestException.class, () -> service.inscribirUsuario(1L, 2L));
    }

    private TorneoDTO requestValido() {
        return TorneoDTO.builder()
                .nombre("Copa Arcade")
                .descripcion("Torneo semanal")
                .maquinaId(1L)
                .horarioId(1L)
                .cuposMaximos(8)
                .build();
    }

    private Torneo torneoProgramado() {
        return Torneo.builder()
                .id(1L)
                .nombre("Copa Arcade")
                .descripcion("Torneo semanal")
                .maquinaId(1L)
                .horarioId(1L)
                .cuposMaximos(8)
                .cuposDisponibles(8)
                .estado("PROGRAMADO")
                .fechaCreacion(LocalDate.now())
                .build();
    }

    private Torneo torneoAbierto() {
        Torneo torneo = torneoProgramado();
        torneo.setEstado("ABIERTO");
        return torneo;
    }

    private InscripcionTorneo inscripcionActiva() {
        return InscripcionTorneo.builder()
                .id(1L)
                .torneoId(1L)
                .usuarioId(2L)
                .estado("INSCRITO")
                .fechaInscripcion(LocalDateTime.now())
                .build();
    }
}
