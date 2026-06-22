package com.juratempest.ms_promociones.controller;

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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.juratempest.ms_promociones.dto.PromocionDTO;
import com.juratempest.ms_promociones.dto.ValidarPromocionResponseDTO;
import com.juratempest.ms_promociones.exception.BadRequestException;
import com.juratempest.ms_promociones.exception.GlobalExceptionHandler;
import com.juratempest.ms_promociones.exception.ResourceNotFoundException;
import com.juratempest.ms_promociones.service.PromocionService;

@ExtendWith(MockitoExtension.class)
class PromocionControllerTest {

    private MockMvc mockMvc;
    private PromocionDTO promocion;

    @Mock
    private PromocionService service;

    @BeforeEach
    void setUp() {
        PromocionController controller = new PromocionController(service);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        promocion = PromocionDTO.builder()
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

    @Test
    void testListar() throws Exception {
        when(service.listar()).thenReturn(List.of(promocion));

        mockMvc.perform(get("/promociones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].codigo").value("ARCADE10"));

        verify(service).listar();
    }

    @Test
    void testBuscarPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(promocion);

        mockMvc.perform(get("/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("ARCADE10"));

        verify(service).buscarPorId(1L);
    }

    @Test
    void testBuscarPorIdNoEncontrado() throws Exception {
        when(service.buscarPorId(99L)).thenThrow(new ResourceNotFoundException("Promocion no encontrada con id 99"));

        mockMvc.perform(get("/promociones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));

        verify(service).buscarPorId(99L);
    }

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(PromocionDTO.class))).thenReturn(promocion);

        mockMvc.perform(post("/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ARCADE10\",\"nombre\":\"Descuento arcade\",\"descripcion\":\"10% descuento\",\"porcentajeDescuento\":10,\"fechaInicio\":\"2026-06-01\",\"fechaFin\":\"2026-12-31\",\"tipo\":\"GENERAL\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("ARCADE10"));

        verify(service).crear(any(PromocionDTO.class));
    }

    @Test
    void testCrearConDatosInvalidos() throws Exception {
        mockMvc.perform(post("/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validaciones.codigo").exists())
                .andExpect(jsonPath("$.validaciones.nombre").exists());

        verifyNoInteractions(service);
    }

    @Test
    void testValidarPromocion() throws Exception {
        when(service.validarPromocion(any())).thenReturn(ValidarPromocionResponseDTO.builder()
                .valida(true)
                .mensaje("Promocion valida")
                .porcentajeDescuento(10)
                .montoDescuento(1000)
                .montoFinal(9000)
                .promocionId(1L)
                .build());

        mockMvc.perform(post("/promociones/validar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ARCADE10\",\"usuarioId\":10,\"reservaId\":20,\"montoOriginal\":10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valida").value(true))
                .andExpect(jsonPath("$.montoDescuento").value(1000));

        verify(service).validarPromocion(any());
    }

    @Test
    void testActualizar() throws Exception {
        when(service.actualizar(any(), any(PromocionDTO.class))).thenReturn(promocion);

        mockMvc.perform(put("/promociones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"ARCADE10\",\"nombre\":\"Descuento arcade\",\"descripcion\":\"10% descuento\",\"porcentajeDescuento\":10,\"fechaInicio\":\"2026-06-01\",\"fechaFin\":\"2026-12-31\",\"tipo\":\"GENERAL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("ARCADE10"));

        verify(service).actualizar(any(), any(PromocionDTO.class));
    }

    @Test
    void testDesactivar() throws Exception {
        PromocionDTO desactivada = promocion;
        desactivada.setActiva(false);
        when(service.desactivar(1L)).thenReturn(desactivada);

        mockMvc.perform(put("/promociones/1/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activa").value(false));

        verify(service).desactivar(1L);
    }

    @Test
    void testEliminar() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/promociones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Promocion eliminada correctamente"));

        verify(service).eliminar(1L);
    }

    @Test
    void testEliminarNoEncontrada() throws Exception {
        doThrow(new BadRequestException("No se puede eliminar")).when(service).eliminar(1L);

        mockMvc.perform(delete("/promociones/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar"));

        verify(service).eliminar(1L);
    }
}
