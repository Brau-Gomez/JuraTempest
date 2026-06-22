package com.juratempest.ms_horarios.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.juratempest.ms_horarios.dto.BloquehorarioDTO;
import com.juratempest.ms_horarios.exception.BadRequestException;
import com.juratempest.ms_horarios.model.BloqueHorario;
import com.juratempest.ms_horarios.repository.BloqueHorarioRepository;

@ExtendWith(MockitoExtension.class)
class BloqueHorarioServiceTest {

    @Mock
    private BloqueHorarioRepository repository;

    @InjectMocks
    private BloqueHorarioService service;

    @Test
    void testListar() {
        // Given
        when(repository.findAll()).thenReturn(List.of(bloque()));

        // When
        List<BloquehorarioDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("DISPONIBLE", resultado.get(0).getEstado());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(bloque()));

        // When
        BloquehorarioDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testCrear() {
        // Given
        when(repository.findByFechaBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        when(repository.save(any(BloqueHorario.class))).thenReturn(bloque());

        // When
        BloquehorarioDTO resultado = service.crear(dto(), null);

        // Then
        assertEquals(1L, resultado.getId());
        verify(repository).save(any(BloqueHorario.class));
    }

    @Test
    void testActualizar() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(bloque()));
        when(repository.findByFechaBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(List.of());
        when(repository.save(any(BloqueHorario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BloquehorarioDTO resultado = service.actualizar(1L, dto());

        // Then
        assertEquals("DISPONIBLE", resultado.getEstado());
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
    void testBuscarDisponibles() {
        // Given
        when(repository.findByDisponibleTrue()).thenReturn(List.of(bloque()));

        // When
        List<BloquehorarioDTO> resultado = service.buscarDisponibles();

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void testValidarRangoFechas() {
        // When / Then
        assertThrows(BadRequestException.class,
                () -> service.buscarPorRango(LocalDate.now().plusDays(1), LocalDate.now()));
    }

    private BloquehorarioDTO dto() {
        return BloquehorarioDTO.builder()
                .fecha(LocalDate.now())
                .horaInicio(LocalTime.of(10, 0))
                .horaFin(LocalTime.of(11, 0))
                .disponible(true)
                .estado("DISPONIBLE")
                .capacidadMaquina(5)
                .cuposDisponibles(5)
                .build();
    }

    private BloqueHorario bloque() {
        return new BloqueHorario(1L, LocalDate.now(), LocalTime.of(10, 0), LocalTime.of(11, 0), true, "DISPONIBLE", 5, 5);
    }
}
