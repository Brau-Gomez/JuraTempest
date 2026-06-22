package com.juratempest.ms_pagos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juratempest.ms_pagos.dto.PagoDTO;
import com.juratempest.ms_pagos.exception.BadRequestException;
import com.juratempest.ms_pagos.exception.GlobalExceptionHandler;
import com.juratempest.ms_pagos.exception.ResourceNotFoundException;
import com.juratempest.ms_pagos.service.PagoService;

@ExtendWith(MockitoExtension.class)
class PagoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PagoService service;

    private PagoDTO pago;
    private PagoDTO request;

    @BeforeEach
    void setUp() {
        PagoController controller = new PagoController(service);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        pago = PagoDTO.builder()
                .id(1L)
                .usuarioId(10L)
                .reservaId(20L)
                .valorNeto(10000)
                .iva(1900)
                .montoDescuento(0)
                .montoFinal(11900)
                .metodoPago("DEBITO")
                .estado("PENDIENTE")
                .build();

        request = PagoDTO.builder()
                .usuarioId(10L)
                .reservaId(20L)
                .metodoPago("debito")
                .build();
    }

    @Test
    void testListar() throws Exception {
        when(service.listar()).thenReturn(List.of(pago));

        mockMvc.perform(get("/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].montoFinal").value(11900));

        verify(service).listar();
    }

    @Test
    void testBuscarPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(pago);

        mockMvc.perform(get("/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.metodoPago").value("DEBITO"));

        verify(service).buscarPorId(1L);
    }

    @Test
    void testBuscarPorIdNoEncontrado() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Pago no encontrado con id 99"));

        mockMvc.perform(get("/pagos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Pago no encontrado con id 99"));

        verify(service).buscarPorId(99L);
    }

    @Test
    void testBuscarPorUsuario() throws Exception {
        when(service.buscarPorUsuario(10L)).thenReturn(List.of(pago));

        mockMvc.perform(get("/pagos/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(10L));

        verify(service).buscarPorUsuario(10L);
    }

    @Test
    void testTotalPagos() throws Exception {
        when(service.totalPagos()).thenReturn(3L);

        mockMvc.perform(get("/pagos/total"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(3L));

        verify(service).totalPagos();
    }

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(PagoDTO.class))).thenReturn(pago);

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.montoFinal").value(11900));

        verify(service).crear(any(PagoDTO.class));
    }

    @Test
    void testCrearConDatosInvalidos() throws Exception {
        PagoDTO invalido = PagoDTO.builder().build();

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validaciones.usuarioId").exists())
                .andExpect(jsonPath("$.validaciones.reservaId").exists())
                .andExpect(jsonPath("$.validaciones.metodoPago").exists());

        verifyNoInteractions(service);
    }

    @Test
    void testCrearConReglaInvalida() throws Exception {
        when(service.crear(any(PagoDTO.class))).thenThrow(new BadRequestException("Metodo de pago no valido"));

        mockMvc.perform(post("/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Metodo de pago no valido"));

        verify(service).crear(any(PagoDTO.class));
    }

    @Test
    void testAprobar() throws Exception {
        PagoDTO aprobado = PagoDTO.builder()
                .id(1L)
                .usuarioId(10L)
                .reservaId(20L)
                .estado("APROBADO")
                .metodoPago("DEBITO")
                .montoFinal(11900)
                .build();
        when(service.aprobar(1L)).thenReturn(aprobado);

        mockMvc.perform(put("/pagos/1/aprobar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("APROBADO"));

        verify(service).aprobar(1L);
    }

    @Test
    void testRechazar() throws Exception {
        PagoDTO rechazado = PagoDTO.builder().id(1L).estado("RECHAZADO").build();
        when(service.rechazar(1L)).thenReturn(rechazado);

        mockMvc.perform(put("/pagos/1/rechazar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"));

        verify(service).rechazar(1L);
    }

    @Test
    void testAnular() throws Exception {
        PagoDTO anulado = PagoDTO.builder().id(1L).estado("ANULADO").build();
        when(service.anular(1L)).thenReturn(anulado);

        mockMvc.perform(put("/pagos/1/anular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ANULADO"));

        verify(service).anular(1L);
    }

    @Test
    void testEliminar() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Pago eliminado correctamente"));

        verify(service).eliminar(1L);
    }

    @Test
    void testEliminarAprobado() throws Exception {
        doThrow(new BadRequestException("No se puede eliminar un pago aprobado")).when(service).eliminar(1L);

        mockMvc.perform(delete("/pagos/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar un pago aprobado"));

        verify(service).eliminar(1L);
    }
}
