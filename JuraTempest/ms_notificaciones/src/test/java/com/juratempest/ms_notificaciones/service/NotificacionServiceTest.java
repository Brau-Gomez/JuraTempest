package com.juratempest.ms_notificaciones.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.juratempest.ms_notificaciones.client.UsuarioClient;
import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.exception.BadRequestException;
import com.juratempest.ms_notificaciones.model.Notificacion;
import com.juratempest.ms_notificaciones.repository.NotificacionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    private NotificacionService service;

    @Mock
    private NotificacionRepository repository;

    @Mock
    private UsuarioClient usuarioClient;

    @BeforeEach
    void setUp() throws Exception {
        service = new NotificacionService(repository, usuarioClient);
    }

    private Notificacion crearNotificacion() {
        return Notificacion.builder()
                .id(1L)
                .usuarioId(10L)
                .titulo("Reserva confirmada")
                .mensaje("Tu reserva fue creada")
                .tipo("RESERVA")
                .canal("SISTEMA")
                .leida(false)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private NotificacionDTO crearDto() {
        return NotificacionDTO.builder()
                .usuarioId(10L)
                .titulo("Reserva confirmada")
                .mensaje("Tu reserva fue creada")
                .tipo("reserva")
                .canal("sistema")
                .build();
    }

    @Test
    void testListar() {
        when(repository.findAll()).thenReturn(List.of(crearNotificacion()));

        List<NotificacionDTO> notificaciones = service.listar();

        assertNotNull(notificaciones);
        assertEquals(1, notificaciones.size());
        assertEquals("RESERVA", notificaciones.get(0).getTipo());

        verify(repository).findAll();
    }

    @Test
    void testBuscarPorId() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.of(crearNotificacion()));

        NotificacionDTO resultado = service.buscarPorId(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Reserva confirmada", resultado.getTitulo());

        verify(repository).findById(id);
    }

    @Test
    void testBuscarPorUsuario() {
        Long usuarioId = 10L;

        when(usuarioClient.usuarioExiste(usuarioId)).thenReturn(true);
        when(repository.findByUsuarioId(usuarioId)).thenReturn(List.of(crearNotificacion()));

        List<NotificacionDTO> resultado = service.buscarPorUsuario(usuarioId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(usuarioId, resultado.get(0).getUsuarioId());

        verify(usuarioClient).usuarioExiste(usuarioId);
        verify(repository).findByUsuarioId(usuarioId);
    }

    @Test
    void testBuscarNoLeidasPorUsuario() {
        Long usuarioId = 10L;

        when(usuarioClient.usuarioExiste(usuarioId)).thenReturn(true);
        when(repository.findByUsuarioIdAndLeidaFalse(usuarioId)).thenReturn(List.of(crearNotificacion()));

        List<NotificacionDTO> resultado = service.buscarNoLeidasPorUsuario(usuarioId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getLeida());

        verify(usuarioClient).usuarioExiste(usuarioId);
        verify(repository).findByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Test
    void testBuscarPorTipo() {
        when(repository.findByTipo("RESERVA")).thenReturn(List.of(crearNotificacion()));

        List<NotificacionDTO> resultado = service.buscarPorTipo("reserva");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("RESERVA", resultado.get(0).getTipo());

        verify(repository).findByTipo("RESERVA");
    }

    @Test
    void testTotalNoLeidasPorUsuario() {
        Long usuarioId = 10L;

        when(usuarioClient.usuarioExiste(usuarioId)).thenReturn(true);
        when(repository.countByUsuarioIdAndLeidaFalse(usuarioId)).thenReturn(3L);

        Long total = service.totalNoLeidasPorUsuario(usuarioId);

        assertEquals(3L, total);

        verify(usuarioClient).usuarioExiste(usuarioId);
        verify(repository).countByUsuarioIdAndLeidaFalse(usuarioId);
    }

    @Test
    void testCrear() {
        NotificacionDTO dto = crearDto();
        Notificacion guardada = crearNotificacion();

        when(usuarioClient.usuarioExiste(dto.getUsuarioId())).thenReturn(true);
        when(repository.save(any(Notificacion.class))).thenReturn(guardada);

        NotificacionDTO resultado = service.crear(dto);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("RESERVA", resultado.getTipo());
        assertEquals("SISTEMA", resultado.getCanal());
        assertFalse(resultado.getLeida());

        verify(usuarioClient).usuarioExiste(dto.getUsuarioId());
        verify(repository).save(any(Notificacion.class));
    }

    @Test
    void testMarcarComoLeida() {
        Notificacion notificacion = crearNotificacion();

        Notificacion leida = crearNotificacion();
        leida.setLeida(true);

        when(repository.findById(1L)).thenReturn(Optional.of(notificacion));
        when(repository.save(any(Notificacion.class))).thenReturn(leida);

        NotificacionDTO resultado = service.marcarComoLeida(1L);

        assertNotNull(resultado);
        assertTrue(resultado.getLeida());

        verify(repository).findById(1L);
        verify(repository).save(any(Notificacion.class));
    }

    @Test
    void testMarcarTodasComoLeidas() {
        Long usuarioId = 10L;
        Notificacion notificacion = crearNotificacion();

        when(usuarioClient.usuarioExiste(usuarioId)).thenReturn(true);
        when(repository.findByUsuarioIdAndLeidaFalse(usuarioId)).thenReturn(List.of(notificacion));

        service.marcarTodasComoLeidas(usuarioId);

        assertTrue(notificacion.getLeida());

        verify(usuarioClient).usuarioExiste(usuarioId);
        verify(repository).findByUsuarioIdAndLeidaFalse(usuarioId);
        verify(repository).saveAll(List.of(notificacion));
    }

    @Test
    void testEliminar() {
        Notificacion notificacion = crearNotificacion();
        notificacion.setLeida(true);

        when(repository.findById(1L)).thenReturn(Optional.of(notificacion));

        service.eliminar(1L);

        verify(repository).findById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void testObtenerNoLeidas() {
        when(repository.findByLeidaFalse()).thenReturn(List.of(crearNotificacion()));

        List<NotificacionDTO> resultado = service.obtenerNoLeidas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertFalse(resultado.get(0).getLeida());

        verify(repository).findByLeidaFalse();
    }

    @Test
    void testBuscarPorTipoInvalido() {
        assertThrows(BadRequestException.class, () -> service.buscarPorTipo("INVALIDO"));

        verify(repository, never()).findByTipo(any());
    }
}