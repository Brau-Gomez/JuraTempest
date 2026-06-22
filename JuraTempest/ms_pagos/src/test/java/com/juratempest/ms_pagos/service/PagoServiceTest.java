package com.juratempest.ms_pagos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.juratempest.ms_pagos.client.FidelizacionClient;
import com.juratempest.ms_pagos.client.MaquinaClient;
import com.juratempest.ms_pagos.client.NotificacionClient;
import com.juratempest.ms_pagos.client.PromocionClient;
import com.juratempest.ms_pagos.client.ReservaClient;
import com.juratempest.ms_pagos.dto.CrearFidelizacionRequestDTO;
import com.juratempest.ms_pagos.dto.CrearNotificacionRequestDTO;
import com.juratempest.ms_pagos.dto.MaquinaResponseDTO;
import com.juratempest.ms_pagos.dto.PagoDTO;
import com.juratempest.ms_pagos.dto.PromocionResponseDTO;
import com.juratempest.ms_pagos.dto.ReservaResponseDTO;
import com.juratempest.ms_pagos.model.Pago;
import com.juratempest.ms_pagos.repository.PagoRepository;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    private PagoService service;

    @Mock
    private PagoRepository repository;
    @Mock
    private ReservaClient reservaClient;
    @Mock
    private MaquinaClient maquinaClient;
    @Mock
    private PromocionClient promocionClient;
    @Mock
    private FidelizacionClient fidelizacionClient;
    @Mock
    private NotificacionClient notificacionClient;

    @BeforeEach
    void setUp() {
        service = new PagoService(repository, reservaClient, maquinaClient, promocionClient, fidelizacionClient, notificacionClient);
    }

    private Pago pagoPendiente() {
        return Pago.builder()
                .id(1L)
                .usuarioId(10L)
                .reservaId(20L)
                .valorNeto(10000)
                .iva(1900)
                .montoDescuento(0)
                .montoFinal(11900)
                .metodoPago("DEBITO")
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private PagoDTO request() {
        return PagoDTO.builder()
                .usuarioId(10L)
                .reservaId(20L)
                .metodoPago("debito")
                .build();
    }

    private ReservaResponseDTO reserva() {
        return ReservaResponseDTO.builder()
                .id(20L)
                .usuarioId(10L)
                .maquinaId(30L)
                .horarioId(40L)
                .estado("ACTIVA")
                .build();
    }

    private MaquinaResponseDTO maquina() {
        return MaquinaResponseDTO.builder()
                .id(30L)
                .estado("ACTIVA")
                .costoPorBloque(10000)
                .build();
    }

    @Test
    void testListar() {
        when(repository.findAll()).thenReturn(List.of(pagoPendiente()));

        List<PagoDTO> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("PENDIENTE", resultado.get(0).getEstado());
        verify(repository).findAll();
    }

    @Test
    void testCrearSinPromocionCalculaIvaYMontoFinal() {
        when(reservaClient.buscarPorId(20L)).thenReturn(reserva());
        when(maquinaClient.buscarPorId(30L)).thenReturn(maquina());
        when(repository.existsByReservaIdAndEstado(20L, "APROBADO")).thenReturn(false);
        when(repository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setId(1L);
            return pago;
        });

        PagoDTO resultado = service.crear(request());

        assertNotNull(resultado);
        assertEquals(10000, resultado.getValorNeto());
        assertEquals(0, resultado.getMontoDescuento());
        assertEquals(1900, resultado.getIva());
        assertEquals(11900, resultado.getMontoFinal());
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals("DEBITO", resultado.getMetodoPago());
        verify(notificacionClient).crearNotificacion(any(CrearNotificacionRequestDTO.class));
    }

    @Test
    void testCrearConPromocionAplicaDescuentoAntesDelIva() {
        PagoDTO dto = request();
        dto.setPromocionId(5L);

        when(reservaClient.buscarPorId(20L)).thenReturn(reserva());
        when(maquinaClient.buscarPorId(30L)).thenReturn(maquina());
        when(repository.existsByReservaIdAndEstado(20L, "APROBADO")).thenReturn(false);
        when(promocionClient.buscarPorId(5L)).thenReturn(PromocionResponseDTO.builder()
                .id(5L)
                .activa(true)
                .porcentajeDescuento(10)
                .fechaInicio(LocalDate.now().minusDays(1))
                .fechaFin(LocalDate.now().plusDays(1))
                .build());
        when(repository.save(any(Pago.class))).thenAnswer(invocation -> {
            Pago pago = invocation.getArgument(0);
            pago.setId(1L);
            return pago;
        });

        PagoDTO resultado = service.crear(dto);

        assertEquals(10000, resultado.getValorNeto());
        assertEquals(1000, resultado.getMontoDescuento());
        assertEquals(1710, resultado.getIva());
        assertEquals(10710, resultado.getMontoFinal());
    }

    @Test
    void testAprobarPagoPendiente() {
        Pago pago = pagoPendiente();

        when(repository.findById(1L)).thenReturn(Optional.of(pago));
        when(repository.existsByReservaIdAndEstadoAndIdNot(20L, "APROBADO", 1L)).thenReturn(false);
        when(repository.save(any(Pago.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagoDTO resultado = service.aprobar(1L);

        assertEquals("APROBADO", resultado.getEstado());
        assertNotNull(resultado.getFechaPago());
        verify(fidelizacionClient).registrarPuntos(any(CrearFidelizacionRequestDTO.class));
        verify(notificacionClient).crearNotificacion(any(CrearNotificacionRequestDTO.class));
    }


    @Test
    void testEliminarPagoPendiente() {
        Pago pago = pagoPendiente();
        when(repository.findById(1L)).thenReturn(Optional.of(pago));

        service.eliminar(1L);

        verify(repository).deleteById(1L);
        assertFalse("APROBADO".equals(pago.getEstado()));
    }

    @Test 
    void testCalcularIVA(){
        int iva = service.calcularIva(1000); 
        assertEquals(190, iva);

        assertThrows(IllegalArgumentException.class, () -> service.calcularIva(-50));
    }

    @Test
    void testCalcularMontoFinal(){
        int subtotal = service.calcularMontoFinal(10000, 1000);
        assertEquals(10710, subtotal);

        assertThrows(IllegalArgumentException.class, () -> service.calcularMontoFinal(-1, 1000));
        assertThrows(IllegalArgumentException.class, () -> service.calcularMontoFinal(10000, -5));
        assertThrows(IllegalArgumentException.class, () -> service.calcularMontoFinal(1000, 1500));
        
    }
}
