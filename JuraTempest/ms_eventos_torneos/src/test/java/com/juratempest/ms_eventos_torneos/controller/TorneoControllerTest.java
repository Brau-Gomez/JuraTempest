package com.juratempest.ms_eventos_torneos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.juratempest.ms_eventos_torneos.dto.InscripcionTorneoDTO;
import com.juratempest.ms_eventos_torneos.dto.TorneoDTO;
import com.juratempest.ms_eventos_torneos.service.TorneoService;

@ExtendWith(MockitoExtension.class)
class TorneoControllerTest {

    @Mock
    private TorneoService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TorneoController(service)).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void testListar() throws Exception {
        // Given
        when(service.listar()).thenReturn(List.of(torneoDTO()));

        // When / Then
        mockMvc.perform(get("/torneos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Given
        when(service.buscarPorId(1L)).thenReturn(torneoDTO());

        // When / Then
        mockMvc.perform(get("/torneos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Copa Arcade"));
    }

    @Test
    void testCrear() throws Exception {
        // Given
        when(service.crear(any(TorneoDTO.class))).thenReturn(torneoDTO());

        // When / Then
        mockMvc.perform(post("/torneos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(torneoDTO())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PROGRAMADO"));
    }

    @Test
    void testInscribirUsuario() throws Exception {
        // Given
        when(service.inscribirUsuario(1L, 2L)).thenReturn(inscripcionDTO());

        // When / Then
        mockMvc.perform(post("/torneos/1/inscribir/2"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("INSCRITO"));
    }

    @Test
    void testFinalizar() throws Exception {
        // Given
        TorneoDTO finalizado = torneoDTO();
        finalizado.setEstado("FINALIZADO");
        finalizado.setGanadorUsuarioId(2L);
        when(service.finalizar(1L, 2L)).thenReturn(finalizado);

        // When / Then
        mockMvc.perform(put("/torneos/1/finalizar/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADO"));
    }

    private TorneoDTO torneoDTO() {
        return TorneoDTO.builder()
                .id(1L)
                .nombre("Copa Arcade")
                .descripcion("Torneo semanal")
                .maquinaId(1L)
                .horarioId(1L)
                .cuposMaximos(8)
                .cuposDisponibles(8)
                .estado("PROGRAMADO")
                .fechaCreacion(LocalDate.now())
                .build();
    }

    private InscripcionTorneoDTO inscripcionDTO() {
        return InscripcionTorneoDTO.builder()
                .id(1L)
                .torneoId(1L)
                .usuarioId(2L)
                .estado("INSCRITO")
                .fechaInscripcion(LocalDateTime.now())
                .build();
    }
}
