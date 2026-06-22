package com.juratempest.ms_usuarios.controller;

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
import com.juratempest.ms_usuarios.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios.dto.UsuarioDTO;
import com.juratempest.ms_usuarios.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService service;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(service)).build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Test
    void testListar() throws Exception {
        // Given
        when(service.listar()).thenReturn(List.of(dto()));

        // When / Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void testBuscarPorId() throws Exception {
        // Given
        when(service.buscarPorId(1L)).thenReturn(dto());

        // When / Then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("cliente@test.cl"));
    }

    @Test
    void testExistePorId() throws Exception {
        // Given
        when(service.existePorId(1L)).thenReturn(true);

        // When / Then
        mockMvc.perform(get("/users/1/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void testBuscarPorEmail() throws Exception {
        // Given
        when(service.buscarPorEmail("cliente@test.cl")).thenReturn(dto());

        // When / Then
        mockMvc.perform(get("/users/email/cliente@test.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cliente"));
    }

    @Test
    void testCrear() throws Exception {
        // Given
        when(service.crearDesdeAdmin(any(RegistroRequestDTO.class))).thenReturn(dto());
        RegistroRequestDTO request = new RegistroRequestDTO(1L, "Cliente", "Prueba", "cliente@test.cl", true);

        // When / Then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testActualizar() throws Exception {
        // Given
        when(service.actualizar(any(Long.class), any(UsuarioDTO.class))).thenReturn(dto());

        // When / Then
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void testEliminar() throws Exception {
        // When / Then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(service).eliminar(1L);
    }

    private UsuarioDTO dto() {
        return UsuarioDTO.builder()
                .id(1L)
                .cuentaId(1L)
                .nombre("Cliente")
                .apellido("Prueba")
                .email("cliente@test.cl")
                .frecuente(true)
                .activo(true)
                .fechaRegistro(LocalDate.now())
                .build();
    }
}
