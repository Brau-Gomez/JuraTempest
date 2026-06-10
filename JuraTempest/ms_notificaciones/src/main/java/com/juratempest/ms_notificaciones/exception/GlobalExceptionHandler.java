package com.juratempest.ms_notificaciones.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.juratempest.ms_notificaciones.dto.ApiErrorDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        // TODO: Construir una respuesta HTTP 404 cuando el recurso solicitado no exista.
        // TODO: Registrar el evento con log.warn y devolver un ApiErrorDTO con timestamp, status, error, mensaje y path.
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        // TODO: Construir una respuesta HTTP 400 cuando la solicitud tenga datos invalidos para la regla de negocio.
        // TODO: Registrar el evento con log.warn y devolver el mensaje controlado al cliente.
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // TODO: Recorrer los errores producidos por @Valid en los DTO.
        // TODO: Crear un mapa campo -> mensaje para que el cliente sepa que dato debe corregir.
        // TODO: Retornar una respuesta HTTP 400 con los errores de validacion.
        Map<String, String> validaciones = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> validaciones.put(error.getField(), error.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Datos de entrada no validos", request.getRequestURI(), validaciones);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneral(Exception ex, HttpServletRequest request) {
        // TODO: Manejar errores inesperados con una respuesta HTTP 500 generica.
        // TODO: Registrar el detalle tecnico en logs, pero no exponer informacion sensible al cliente.
        log.error("Error inesperado path={}", request.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR INTERNO DEL SERVICIO", request.getRequestURI(), null);
    }

    private ResponseEntity<ApiErrorDTO> error(HttpStatus status, String mensaje, String path, Map<String, String> validaciones) {
        // TODO: Centralizar la construccion de ApiErrorDTO para mantener el mismo formato en todos los errores.
        ApiErrorDTO body = ApiErrorDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .mensaje(mensaje)
                .path(path)
                .validaciones(validaciones)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
