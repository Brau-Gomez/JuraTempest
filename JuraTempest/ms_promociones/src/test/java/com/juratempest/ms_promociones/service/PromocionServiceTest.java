package com.juratempest.ms_promociones.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.juratempest.ms_promociones.dto.PromocionDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionRequestDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionResponseDTO;
import com.juratempest.ms_promociones.exception.BadRequestException;
import com.juratempest.ms_promociones.model.Promocion;
import com.juratempest.ms_promociones.repository.PromocionRepository;

@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    private PromocionService service;

    @Mock
    private PromocionRepository repository;

    @BeforeEach
    void setUp() {
        service = new PromocionService(repository);
    }

    private Promocion promocion() {
        return Promocion.builder()
                .id(1L)
                .codigo("ARCADE10")
                .nombre("Descuento arcade")
                .descripcion("10% de descuento general")
                .porcentajeDescuento(10)
                .fechaInicio(LocalDate.now().minusDays(1))
                .fechaFin(LocalDate.now().plusDays(10))
                .activa(true)
                .tipo("GENERAL")
                .build();
    }

    private PromocionDTO request() {
        return PromocionDTO.builder()
                .codigo("arcade10")
                .nombre("Descuento arcade")
                .descripcion("10% de descuento general")
                .porcentajeDescuento(10)
                .fechaInicio(LocalDate.now().minusDays(1))
                .fechaFin(LocalDate.now().plusDays(10))
                .tipo("general")
                .build();
    }

    @Test
    void testListar() {
        when(repository.findAll()).thenReturn(List.of(promocion()));

        List<PromocionDTO> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("ARCADE10", resultado.get(0).getCodigo());
        verify(repository).findAll();
    }

    @Test
    void testBuscarPorId() {
        when(repository.findById(1L)).thenReturn(Optional.of(promocion()));

        PromocionDTO resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(repository).findById(1L);
    }

    @Test
    void testBuscarPorCodigo() {
        when(repository.findByCodigo("ARCADE10")).thenReturn(Optional.of(promocion()));

        PromocionDTO resultado = service.buscarPorCodigo("arcade10");

        assertEquals("ARCADE10", resultado.getCodigo());
        verify(repository).findByCodigo("ARCADE10");
    }

    @Test
    void testCrearNormalizaCodigoTipoYActivaPorDefecto() {
        when(repository.existsByCodigo("ARCADE10")).thenReturn(false);
        when(repository.save(any(Promocion.class))).thenAnswer(invocation -> {
            Promocion promocion = invocation.getArgument(0);
            promocion.setId(1L);
            return promocion;
        });

        PromocionDTO resultado = service.crear(request());

        assertEquals("ARCADE10", resultado.getCodigo());
        assertEquals("GENERAL", resultado.getTipo());
        assertEquals(true, resultado.getActiva());
        verify(repository).save(any(Promocion.class));
    }

    @Test
    void testValidarPromocionCalculaDescuento() {
        when(repository.findByCodigo("ARCADE10")).thenReturn(Optional.of(promocion()));

        ValidarPromocionResponseDTO resultado = service.validarPromocion(ValidarPromocionRequestDTO.builder()
                .codigo("arcade10")
                .usuarioId(10L)
                .reservaId(20L)
                .montoOriginal(10000)
                .build());

        assertEquals(true, resultado.getValida());
        assertEquals(1000, resultado.getMontoDescuento());
        assertEquals(9000, resultado.getMontoFinal());
    }

    @Test
    void testActivar() {
        Promocion promocion = promocion();
        promocion.setActiva(false);
        when(repository.findById(1L)).thenReturn(Optional.of(promocion));
        when(repository.save(any(Promocion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PromocionDTO resultado = service.activar(1L);

        assertEquals(true, resultado.getActiva());
    }

    @Test
    void testEliminar() {
        when(repository.findById(1L)).thenReturn(Optional.of(promocion()));

        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void testCalcularDescuento() {
        assertEquals(1500, service.calcularDescuento(10000, 15));
        assertThrows(BadRequestException.class, () -> service.calcularDescuento(0, 10));
        assertThrows(IllegalArgumentException.class, () -> service.calcularDescuento(10000, 101));
    }
}
