package com.juratempest.ms_reservas.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.juratempest.ms_reservas.client.ReservaClient;
import com.juratempest.ms_reservas.dto.ReservaDTO;
import com.juratempest.ms_reservas.exception.ResourceNotFoundException;
import com.juratempest.ms_reservas.model.Reserva;
import com.juratempest.ms_reservas.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository repository;
    @Mock
    private ReservaClient reservaClient;

    @InjectMocks
    private ReservaService service;

    @Test
    void testListar() {
        // Given
        when(repository.findAll()).thenReturn(List.of(reserva()));

        // When
        List<ReservaDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(reserva()));

        // When
        ReservaDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testCrear() {
        // Given
        mockValidacionesRemotas();
        when(repository.existsByMaquinaIdAndHorarioId(1L, 1L)).thenReturn(false);
        when(repository.save(any(Reserva.class))).thenReturn(reserva());

        // When
        ReservaDTO resultado = service.crear(dto());

        // Then
        assertEquals("ACTIVA", resultado.getEstado());
        verify(repository).save(any(Reserva.class));
    }

    @Test
    void testActualizar() {
        // Given
        mockValidacionesRemotas();
        when(repository.findById(1L)).thenReturn(Optional.of(reserva()));
        when(repository.existsByMaquinaIdAndHorarioIdAndIdNot(1L, 1L, 1L)).thenReturn(false);
        when(repository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReservaDTO resultado = service.actualizar(1L, dto());

        // Then
        assertEquals("ACTIVA", resultado.getEstado());
    }

    @Test
    void testEliminar() {
        // Given
        when(repository.existsById(1L)).thenReturn(true);

        // When
        service.eliminar(1L);

        // Then
        verify(repository).deleteById(1L);
    }

    @Test
    void testBuscarPorUsuario() {
        // Given
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(reserva()));

        // When
        List<ReservaDTO> resultado = service.buscarPorUsuario(1L);

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void testBuscarPorEstado() {
        // Given
        when(repository.findByEstado("ACTIVA")).thenReturn(List.of(reserva()));

        // When
        List<ReservaDTO> resultado = service.buscarPorEstado("activa");

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void testValidarUsuarioRemoto() {
        // Given
        when(reservaClient.usuarioExiste(1L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> service.crear(dto()));
    }

    private void mockValidacionesRemotas() {
        when(reservaClient.usuarioExiste(1L)).thenReturn(true);
        when(reservaClient.maquinaActiva(1L)).thenReturn(true);
        when(reservaClient.bloqueExiste(1L)).thenReturn(true);
    }

    private ReservaDTO dto() {
        return ReservaDTO.builder()
                .usuarioId(1L)
                .maquinaId(1L)
                .horarioId(1L)
                .estado("ACTIVA")
                .build();
    }

    private Reserva reserva() {
        return new Reserva(1L, 1L, 1L, 1L, LocalDate.now(), "ACTIVA");
    }
}
