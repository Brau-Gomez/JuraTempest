package com.juratempest.ms_maquinas.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.juratempest.ms_maquinas.dto.ApiErrorDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;



@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Maneja recursos no encontrados, como una maquina con id inexistente.
    // Responde 404 para expresar que la solicitud apunta a un recurso que no esta disponible.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> manejarNotFound(ResourceNotFoundException ex, HttpServletRequest request){
        log.warn("Recurso no encontrado path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return error(HttpStatus.NOT_FOUND, 
            ex.getMessage(), 
            request.getRequestURI(), 
            null);
    }

    // Maneja errores de validacion generados por @Valid en los DTO.
    // Recopila mensajes por campo para facilitar la correccion de datos enviados por el cliente.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request){
        Map<String,String> validaciones= new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
            validaciones.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validacion fallida path={} errores={}", request.getRequestURI(), validaciones);
        return error(HttpStatus.BAD_REQUEST, 
            "Datos de entrada no validos", 
            request.getRequestURI(), 
            validaciones);
    }

    //Maneja cualquier error no previsto.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> manejarGeneric(Exception ex, HttpServletRequest request){
        log.error("Error inesperado path={}", request.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "ERROR INTERNO DEL SERVICIO",
            request.getRequestURI(),
            null
        );
    }

    //Maneja errores de datos mal ingresados por el cliente.
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> manejarBadRequest(BadRequestException ex, HttpServletRequest request){
        log.warn("Solicitud invalida path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, 
            ex.getMessage(), 
            request.getRequestURI(), 
            null);
    }




    // Construye el formato comun de error para el microservicio de maquinas.
    // Centralizar este Map evita repetir estructura en cada tipo de excepcion.
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
