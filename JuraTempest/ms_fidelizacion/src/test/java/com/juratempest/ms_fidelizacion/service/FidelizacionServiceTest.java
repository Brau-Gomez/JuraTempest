package com.juratempest.ms_fidelizacion.service;

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

import com.juratempest.ms_fidelizacion.client.UsuarioClient;
import com.juratempest.ms_fidelizacion.dto.FidelizacionDTO;
import com.juratempest.ms_fidelizacion.exception.ResourceNotFoundException;
import com.juratempest.ms_fidelizacion.model.Fidelizacion;
import com.juratempest.ms_fidelizacion.repository.FidelizacionRepository;

@ExtendWith(MockitoExtension.class)
class FidelizacionServiceTest {

    @Mock
    private FidelizacionRepository repository;
    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private FidelizacionService service;

    @Test
    void testListar() {
        // Given
        when(repository.findAll()).thenReturn(List.of(registro()));

        // When
        List<FidelizacionDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(registro()));

        // When
        FidelizacionDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testCrear() {
        // Given
        when(usuarioClient.usuarioExiste(1L)).thenReturn(true);
        when(repository.save(any(Fidelizacion.class))).thenReturn(registro());

        // When
        FidelizacionDTO resultado = service.crear(dto());

        // Then
        assertEquals(50, resultado.getPuntos());
    }

    @Test
    void testActualizar() {
        // Given
        when(usuarioClient.usuarioExiste(1L)).thenReturn(true);
        when(repository.findById(1L)).thenReturn(Optional.of(registro()));
        when(repository.save(any(Fidelizacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        FidelizacionDTO resultado = service.actualizar(1L, dto());

        // Then
        assertEquals(50, resultado.getPuntos());
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
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(registro()));

        // When
        List<FidelizacionDTO> resultado = service.buscarPorUsuario(1L);

        // Then
        assertEquals(1, resultado.size());
    }

    @Test
    void testTotalPuntos() {
        // Given
        when(repository.findByUsuarioId(1L)).thenReturn(List.of(registro(), new Fidelizacion(2L, 1L, 25, "Bono", LocalDate.now())));

        // When
        Long resultado = service.totalPuntos(1L);

        // Then
        assertEquals(75L, resultado);
    }

    @Test
    void testValidarUsuarioRemoto() {
        // Given
        when(usuarioClient.usuarioExiste(1L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> service.crear(dto()));
    }

    private FidelizacionDTO dto() {
        return FidelizacionDTO.builder()
                .usuarioId(1L)
                .puntos(50)
                .descripcion("Compra")
                .fechaRegistro(LocalDate.now())
                .build();
    }

    private Fidelizacion registro() {
        return new Fidelizacion(1L, 1L, 50, "Compra", LocalDate.now());
    }
}
