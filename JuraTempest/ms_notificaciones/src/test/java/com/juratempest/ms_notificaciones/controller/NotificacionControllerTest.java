package com.juratempest.ms_notificaciones.controller;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juratempest.ms_notificaciones.dto.NotificacionDTO;
import com.juratempest.ms_notificaciones.exception.BadRequestException;
import com.juratempest.ms_notificaciones.exception.GlobalExceptionHandler;
import com.juratempest.ms_notificaciones.exception.ResourceNotFoundException;
import com.juratempest.ms_notificaciones.service.NotificacionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificacionService service;

    private ObjectMapper objectMapper;

    private NotificacionDTO notificacion;
    private NotificacionDTO request;

    @BeforeEach
    void setUp() {
        NotificacionController controller = new NotificacionController(service);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();

        notificacion = NotificacionDTO.builder()
                .id(1L)
                .usuarioId(10L)
                .titulo("Reserva confirmada")
                .mensaje("Tu reserva fue creada")
                .tipo("RESERVA")
                .canal("SISTEMA")
                .leida(false)
                .fechaCreacion(null)
                .build();

        request = NotificacionDTO.builder()
                .usuarioId(10L)
                .titulo("Reserva confirmada")
                .mensaje("Tu reserva fue creada")
                .tipo("reserva")
                .canal("sistema")
                .build();
    }

    @Test
    void testListar() throws Exception {
        when(service.listar()).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].usuarioId").value(10L))
                .andExpect(jsonPath("$[0].titulo").value("Reserva confirmada"))
                .andExpect(jsonPath("$[0].tipo").value("RESERVA"))
                .andExpect(jsonPath("$[0].canal").value("SISTEMA"))
                .andExpect(jsonPath("$[0].leida").value(false));

        verify(service).listar();
    }

    @Test
    void testBuscarPorId() throws Exception {
        when(service.buscarPorId(1L)).thenReturn(notificacion);

        mockMvc.perform(get("/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(10L))
                .andExpect(jsonPath("$.titulo").value("Reserva confirmada"))
                .andExpect(jsonPath("$.tipo").value("RESERVA"));

        verify(service).buscarPorId(1L);
    }

    @Test
    void testBuscarPorIdNoEncontrado() throws Exception {
        when(service.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Notificacion no encontrada con id 99"));

        mockMvc.perform(get("/notificaciones/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensaje").value("Notificacion no encontrada con id 99"))
                .andExpect(jsonPath("$.path").value("/notificaciones/99"));

        verify(service).buscarPorId(99L);
    }

    @Test
    void testBuscarPorUsuario() throws Exception {
        when(service.buscarPorUsuario(10L)).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/notificaciones/usuario/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(10L))
                .andExpect(jsonPath("$[0].tipo").value("RESERVA"));

        verify(service).buscarPorUsuario(10L);
    }

    @Test
    void testBuscarNoLeidasPorUsuario() throws Exception {
        when(service.buscarNoLeidasPorUsuario(10L)).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/notificaciones/usuario/10/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(10L))
                .andExpect(jsonPath("$[0].leida").value(false));

        verify(service).buscarNoLeidasPorUsuario(10L);
    }

    @Test
    void testTotalNoLeidas() throws Exception {
        when(service.totalNoLeidasPorUsuario(10L)).thenReturn(3L);

        mockMvc.perform(get("/notificaciones/usuario/10/total-no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalNoLeidas").value(3L));

        verify(service).totalNoLeidasPorUsuario(10L);
    }

    @Test
    void testBuscarPorTipo() throws Exception {
        when(service.buscarPorTipo("RESERVA")).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/notificaciones/tipo/RESERVA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("RESERVA"));

        verify(service).buscarPorTipo("RESERVA");
    }

    @Test
    void testBuscarPorTipoInvalido() throws Exception {
        when(service.buscarPorTipo("INVALIDO"))
                .thenThrow(new BadRequestException("Tipo no valido"));

        mockMvc.perform(get("/notificaciones/tipo/INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensaje").value("Tipo no valido"));

        verify(service).buscarPorTipo("INVALIDO");
    }

    @Test
    void testBuscarNoLeidas() throws Exception {
        when(service.obtenerNoLeidas()).thenReturn(List.of(notificacion));

        mockMvc.perform(get("/notificaciones/no-leidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leida").value(false));

        verify(service).obtenerNoLeidas();
    }

    @Test
    void testCrear() throws Exception {
        when(service.crear(any(NotificacionDTO.class))).thenReturn(notificacion);

        mockMvc.perform(post("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.usuarioId").value(10L))
                .andExpect(jsonPath("$.titulo").value("Reserva confirmada"))
                .andExpect(jsonPath("$.tipo").value("RESERVA"))
                .andExpect(jsonPath("$.canal").value("SISTEMA"))
                .andExpect(jsonPath("$.leida").value(false));

        verify(service).crear(any(NotificacionDTO.class));
    }

    @Test
    void testCrearConDatosInvalidos() throws Exception {
        NotificacionDTO invalida = NotificacionDTO.builder().build();

        mockMvc.perform(post("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Datos de entrada no validos"))
                .andExpect(jsonPath("$.validaciones.usuarioId").exists())
                .andExpect(jsonPath("$.validaciones.titulo").exists())
                .andExpect(jsonPath("$.validaciones.mensaje").exists())
                .andExpect(jsonPath("$.validaciones.tipo").exists())
                .andExpect(jsonPath("$.validaciones.canal").exists());

        verifyNoInteractions(service);
    }

    @Test
    void testCrearUsuarioNoExiste() throws Exception {
        when(service.crear(any(NotificacionDTO.class)))
                .thenThrow(new ResourceNotFoundException("Usuario no existe"));

        mockMvc.perform(post("/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Usuario no existe"));

        verify(service).crear(any(NotificacionDTO.class));
    }

    @Test
    void testMarcarComoLeida() throws Exception {
        NotificacionDTO leida = NotificacionDTO.builder()
                .id(1L)
                .usuarioId(10L)
                .titulo("Reserva confirmada")
                .mensaje("Tu reserva fue creada")
                .tipo("RESERVA")
                .canal("SISTEMA")
                .leida(true)
                .fechaCreacion(null)
                .build();

        when(service.marcarComoLeida(1L)).thenReturn(leida);

        mockMvc.perform(put("/notificaciones/1/leer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.leida").value(true));

        verify(service).marcarComoLeida(1L);
    }

    @Test
    void testMarcarComoLeidaYaLeida() throws Exception {
        when(service.marcarComoLeida(1L))
                .thenThrow(new BadRequestException("Notificacion ya marcada como leida"));

        mockMvc.perform(put("/notificaciones/1/leer"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Notificacion ya marcada como leida"));

        verify(service).marcarComoLeida(1L);
    }

    @Test
    void testMarcarTodasComoLeidas() throws Exception {
        doNothing().when(service).marcarTodasComoLeidas(10L);

        mockMvc.perform(put("/notificaciones/usuario/10/leer-todas"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notificaciones marcadas como leidas"));

        verify(service).marcarTodasComoLeidas(10L);
    }

    @Test
    void testEliminar() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Notificacion eliminada correctamente"));

        verify(service).eliminar(1L);
    }

    @Test
    void testEliminarNoLeida() throws Exception {
        doThrow(new BadRequestException("No se puede eliminar una notificacion no leida"))
                .when(service).eliminar(1L);

        mockMvc.perform(delete("/notificaciones/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("No se puede eliminar una notificacion no leida"));

        verify(service).eliminar(1L);
    }
}