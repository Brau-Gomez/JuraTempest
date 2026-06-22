package com.juratempest.ms_maquinas.controller;

import static org.mockito.ArgumentMatchers.any;
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
import com.juratempest.ms_maquinas.dto.MaquinaDTO;
import com.juratempest.ms_maquinas.service.MaquinaService;

@ExtendWith(MockitoExtension.class)
class MaquinaControllerTest {

    @Mock
    private MaquinaService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MaquinaController(service)).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void testListar() throws Exception {
        // Given
        when(service.listar()).thenReturn(List.of(dto()));

        // When / Then
        mockMvc.perform(get("/maquinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Given
        when(service.buscarPorId(1L)).thenReturn(dto());

        // When / Then
        mockMvc.perform(get("/maquinas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Arcade 1"));
    }

    @Test
    void testCrear() throws Exception {
        // Given
        when(service.crear(any(MaquinaDTO.class))).thenReturn(dto());

        // When / Then
        mockMvc.perform(post("/maquinas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVA"));
    }

    @Test
    void testActualizar() throws Exception {
        // Given
        when(service.actualizar(any(Long.class), any(MaquinaDTO.class))).thenReturn(dto());

        // When / Then
        mockMvc.perform(put("/maquinas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testEliminar() throws Exception {
        // When / Then
        mockMvc.perform(delete("/maquinas/1"))
                .andExpect(status().isOk());
        verify(service).eliminar(1L);
    }

    private MaquinaDTO dto() {
        return MaquinaDTO.builder()
                .id(1L)
                .nombre("Arcade 1")
                .tipo("PELEA")
                .ubicacion("Sala A")
                .estado("ACTIVA")
                .costoPorBloque(5000)
                .fechaInstalacion(LocalDate.now())
                .build();
    }
}
