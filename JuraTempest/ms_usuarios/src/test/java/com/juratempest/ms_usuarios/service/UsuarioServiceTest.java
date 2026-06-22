package com.juratempest.ms_usuarios.service;

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

import com.juratempest.ms_usuarios.dto.RegistroRequestDTO;
import com.juratempest.ms_usuarios.dto.UsuarioDTO;
import com.juratempest.ms_usuarios.exception.ResourceNotFoundException;
import com.juratempest.ms_usuarios.model.Usuario;
import com.juratempest.ms_usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void testListar() {
        // Given
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario()));

        // When
        List<UsuarioDTO> resultado = service.listar();

        // Then
        assertEquals(1, resultado.size());
        assertEquals("cliente@test.cl", resultado.get(0).getEmail());
    }

    @Test
    void testBuscarPorId() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario()));

        // When
        UsuarioDTO resultado = service.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
    }

    @Test
    void testExistePorId() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When / Then
        assertEquals(true, service.existePorId(1L));
    }

    @Test
    void testBuscarPorEmail() {
        // Given
        when(usuarioRepository.findByEmail("cliente@test.cl")).thenReturn(Optional.of(usuario()));

        // When
        UsuarioDTO resultado = service.buscarPorEmail("cliente@test.cl");

        // Then
        assertEquals("Cliente", resultado.getNombre());
    }

    @Test
    void testCrearDesdeAdmin() {
        // Given
        RegistroRequestDTO request = new RegistroRequestDTO(1L, "Cliente", "Prueba", "cliente@test.cl", true);
        when(usuarioRepository.existsByCuentaId(1L)).thenReturn(false);
        when(usuarioRepository.existsByEmail("cliente@test.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario());

        // When
        UsuarioDTO resultado = service.crearDesdeAdmin(request);

        // Then
        assertEquals("cliente@test.cl", resultado.getEmail());
    }

    @Test
    void testEliminar() {
        // Given
        when(usuarioRepository.existsById(1L)).thenReturn(true);

        // When
        service.eliminar(1L);

        // Then
        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void testBuscarInexistenteLanzaError() {
        // Given
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(99L));
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setCuentaId(1L);
        usuario.setNombre("Cliente");
        usuario.setApellido("Prueba");
        usuario.setEmail("cliente@test.cl");
        usuario.setFrecuente(true);
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDate.now());
        return usuario;
    }
}
