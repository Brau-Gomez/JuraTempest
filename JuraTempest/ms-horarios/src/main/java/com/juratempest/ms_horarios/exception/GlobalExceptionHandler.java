package com.juratempest.ms_horarios.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.juratempest.ms_horarios.dto.ApiErrorDTO;

import jakarta.servlet.http.HttpServletRequest;



@ControllerAdvice
public class GlobalExceptionHandler {

    // Maneja recursos inexistentes, como un bloque horario que no se encuentra.
    // Responde 404 para separar errores de busqueda de errores de validacion.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> manejarNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND,
            ex.getMessage(),
            request.getRequestURI(),
            null);
    }


    // Maneja errores generados por @Valid en el DTO.
    // Agrega el detalle por campo para que el cliente sepa que dato debe corregir.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request){
        
        Map<String, String> validaciones = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> validaciones.put(error.getField(), error.getDefaultMessage()));
        
        return error(HttpStatus.BAD_REQUEST,
            "Datos de entrada no validos",
            request.getRequestURI(),
            validaciones
        );
    }

    // Maneja errores de negocio como horarios solapados o rangos invalidos.
    // Usamos HTTP 400 porque la peticion no cumple las reglas del dominio.
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> manejarBadRequest(BadRequestException ex, HttpServletRequest request) {
    return error(HttpStatus.BAD_REQUEST,
        ex.getMessage(),
        request.getRequestURI(),
        null);
    }
    
    // Maneja cualquier excepcion no prevista.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> manejarGeneric(Exception ex, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servicio", request.getRequestURI(), null);
    }

    // Construye el formato comun de error del microservicio de horarios.
    // Centralizar esta estructura mejora consistencia entre respuestas.
    private ResponseEntity<ApiErrorDTO> error(HttpStatus status, String mensaje, String path, Map<String, String> validaciones){
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
