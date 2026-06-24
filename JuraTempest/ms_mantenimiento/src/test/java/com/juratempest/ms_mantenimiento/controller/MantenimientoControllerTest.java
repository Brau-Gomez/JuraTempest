package com.juratempest.ms_mantenimiento.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.juratempest.ms_mantenimiento.dto.MantenimientoDTO;
import com.juratempest.ms_mantenimiento.service.MantenimientoService;

@ExtendWith(MockitoExtension.class)
class MantenimientoControllerTest {

    @Mock
    private MantenimientoService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MantenimientoController(service)).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void testListar() throws Exception {
        // Given
        when(service.listar()).thenReturn(List.of(dtoValido()));

        // When / Then
        mockMvc.perform(get("/mantenimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Given
        when(service.buscarPorId(1L)).thenReturn(dtoValido());

        // When / Then
        mockMvc.perform(get("/mantenimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void testCrear() throws Exception {
        // Given
        when(service.crear(any(MantenimientoDTO.class))).thenReturn(dtoValido());

        // When / Then
        mockMvc.perform(post("/mantenimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoValido())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testIniciar() throws Exception {
        // Given
        MantenimientoDTO iniciado = dtoValido();
        iniciado.setEstado("EN_PROCESO");
        when(service.iniciar(1L)).thenReturn(iniciado);

        // When / Then
        mockMvc.perform(put("/mantenimientos/1/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PROCESO"));
    }

    @Test
    void testEliminar() throws Exception {
        // Given
        doNothing().when(service).eliminar(1L);

        // When / Then
        mockMvc.perform(delete("/mantenimientos/1"))
                .andExpect(status().isOk());
        verify(service).eliminar(1L);
    }

    private MantenimientoDTO dtoValido() {
        return MantenimientoDTO.builder()
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
