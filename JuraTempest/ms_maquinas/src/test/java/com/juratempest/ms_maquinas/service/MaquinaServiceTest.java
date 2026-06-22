package com.juratempest.ms_maquinas.service;

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

import com.juratempest.ms_maquinas.dto.MaquinaDTO;
import com.juratempest.ms_maquinas.exception.ResourceNotFoundException;
import com.juratempest.ms_maquinas.model.Maquina;
import com.juratempest.ms_maquinas.repository.MaquinaRepository;

@ExtendWith(MockitoExtension.class)
class MaquinaServiceTest {

    @Mock
    private MaquinaRepository repository;

    @InjectMocks
    private MaquinaService service;

    @Test
    void testListar() {
        // Given
        when(repository.findAll()).thenReturn(List.of(maquina()));

        // When
        List<MaquinaDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ACTIVA", resultado.get(0).getEstado());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(maquina()));

        // When
        MaquinaDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
        assertEquals("Arcade 1", resultado.getNombre());
    }

    @Test
    void testCrear() {
        // Given
        when(repository.save(any(Maquina.class))).thenReturn(maquina());

        // When
        MaquinaDTO resultado = service.crear(dto());

        // Then
        assertEquals(1L, resultado.getId());
        verify(repository).save(any(Maquina.class));
    }

    @Test
    void testActualizar() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(maquina()));
        when(repository.save(any(Maquina.class))).thenAnswer(invocation -> invocation.getArgument(0));
        MaquinaDTO dto = dto();
        dto.setNombre("Arcade Actualizada");

        // When
        MaquinaDTO resultado = service.actualizar(1L, dto);

        // Then
        assertEquals("Arcade Actualizada", resultado.getNombre());
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
    void testBuscarPorEstado() {
        // Given
        when(repository.findByEstado("ACTIVA")).thenReturn(List.of(maquina()));

        // When
        List<MaquinaDTO> resultado = service.buscarPorEstado("activa");

        // Then
        assertEquals(1, resultado.size());
        assertEquals("ACTIVA", resultado.get(0).getEstado());
    }

    @Test
    void testBuscarInexistenteLanzaError() {
        // Given
        when(repository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
    }

    private MaquinaDTO dto() {
        return MaquinaDTO.builder()
                .nombre("Arcade 1")
                .tipo("PELEA")
                .ubicacion("Sala A")
                .estado("ACTIVA")
                .costoPorBloque(5000)
                .fechaInstalacion(LocalDate.now())
                .build();
    }

    private Maquina maquina() {
        return new Maquina(1L, "Arcade 1", "PELEA", "Sala A", "ACTIVA", 5000, LocalDate.now());
    }
}
