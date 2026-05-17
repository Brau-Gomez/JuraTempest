package com.juratempest.ms_fidelizacion.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.juratempest.ms_fidelizacion.dto.ApiErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura cuando no se encuentra un registro o una referencia necesaria.
    // Responde 404 para indicar que el recurso solicitado no existe.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> manejarNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
                return error(HttpStatus.NOT_FOUND,
                    ex.getMessage(),
                    request.getRequestURI(),
                    null
                );
    }

    // Captura errores de validacion producidos por @Valid en los DTO.
    // Responde 400 porque el problema esta en los datos enviados por el cliente.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> manejarValidacion(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
                Map<String, String> validaciones = new HashMap<>();
                ex.getBindingResult().getFieldErrors().forEach(error -> validaciones.put(error.getField(), error.getDefaultMessage()));

                return error(HttpStatus.BAD_REQUEST,
                    "Datos de entrada no validos",
                    request.getRequestURI(),
                    validaciones
                );
    }

    //Maneja los datos erroneos ingresados por el cliente.
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> manejarBadRequest(BadRequestException ex, HttpServletRequest request){
        return error(HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            request.getRequestURI(),
            null
        );
    }

    //Maneja cualquier error no previsto 
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> manejarGeneric(Exception ex, HttpServletRequest request){
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
            "Error interno del servicio",
            request.getRequestURI(),
            null
        );
    }

    // Construye un cuerpo de error uniforme para este microservicio.
    // Centralizar este armado evita repetir el mismo Map en cada handler.
    private ResponseEntity<ApiErrorDTO> error(HttpStatus status, String mensaje, String path, Map<String, String> validaciones) {
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
