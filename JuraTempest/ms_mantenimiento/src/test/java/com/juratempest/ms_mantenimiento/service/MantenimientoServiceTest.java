package com.juratempest.ms_mantenimiento.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.juratempest.ms_mantenimiento.client.MaquinaClient;
import com.juratempest.ms_mantenimiento.client.NotificacionClient;
import com.juratempest.ms_mantenimiento.client.UsuarioClient;
import com.juratempest.ms_mantenimiento.dto.MantenimientoDTO;
import com.juratempest.ms_mantenimiento.exception.BadRequestException;
import com.juratempest.ms_mantenimiento.model.Mantenimiento;
import com.juratempest.ms_mantenimiento.repository.MantenimientoRepository;

@ExtendWith(MockitoExtension.class)
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository repository;
    @Mock
    private MaquinaClient maquinaClient;
    @Mock
    private UsuarioClient usuarioClient;
    @Mock
    private NotificacionClient notificacionClient;

    @InjectMocks
    private MantenimientoService service;

    @Test
    void testListar() {
        // Given
        when(repository.findAll()).thenReturn(List.of(mantenimientoPendiente()));

        // When
        List<MantenimientoDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(mantenimientoPendiente()));

        // When
        MantenimientoDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
        assertEquals("PREVENTIVO", resultado.getTipo());
    }

    @Test
    void testCrear() {
        // Given
        MantenimientoDTO request = requestValido();
        Mantenimiento guardado = mantenimientoPendiente();
        when(maquinaClient.existe(1L)).thenReturn(true);
        when(usuarioClient.usuarioExiste(1L)).thenReturn(true);
        when(repository.save(any(Mantenimiento.class))).thenReturn(guardado);
        doNothing().when(notificacionClient).crearNotificacion(any());

        // When
        MantenimientoDTO resultado = service.crear(request);

        // Then
        assertEquals(1L, resultado.getId());
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(repository).save(any(Mantenimiento.class));
    }

    @Test
    void testIniciar() {
        // Given
        Mantenimiento mantenimiento = mantenimientoPendiente();
        when(repository.findById(1L)).thenReturn(Optional.of(mantenimiento));
        when(repository.save(any(Mantenimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MantenimientoDTO resultado = service.iniciar(1L);

        // Then
        assertEquals("EN_PROCESO", resultado.getEstado());
    }

    @Test
    void testCerrar() {
        // Given
        Mantenimiento mantenimiento = mantenimientoPendiente();
        mantenimiento.setEstado("EN_PROCESO");
        when(repository.findById(1L)).thenReturn(Optional.of(mantenimiento));
        when(repository.save(any(Mantenimiento.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MantenimientoDTO resultado = service.cerrar(1L);

        // Then
        assertEquals("FINALIZADO", resultado.getEstado());
        assertEquals(LocalDate.now(), resultado.getFechaFin());
    }

    @Test
    void testEliminar() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(mantenimientoPendiente()));

        // When
        service.eliminar(1L);

        // Then
        verify(repository).deleteById(1L);
    }

    @Test
    void testValidarCostoNegativo() {
        // Given
        MantenimientoDTO request = requestValido();
        request.setCosto(-1);

        // When / Then
        assertThrows(BadRequestException.class, () -> service.crear(request));
    }

    private MantenimientoDTO requestValido() {
        return MantenimientoDTO.builder()
                .maquinaId(1L)
                .usuarioOperadorId(1L)
                .tipo("preventivo")
                .descripcion("Limpieza general")
                .tecnico("Equipo Tecnico Norte")
                .costo(25000)
                .build();
    }

    private Mantenimiento mantenimientoPendiente() {
        return Mantenimiento.builder()
                .id(1L)
                .maquinaId(1L)
                .usuarioOperadorId(1L)
                .tipo("PREVENTIVO")
                .descripcion("Limpieza general")
                .tecnico("Equipo Tecnico Norte")
                .estado("PENDIENTE")
                .fechaInicio(LocalDate.now())
                .costo(25000)
                .build();
    }
}
