package com.juratempest.ms_pagos.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.juratempest.ms_pagos.dto.ApiErrorDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // Captura errores de recursos inexistentes y responde con HTTP 404.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        
        log.warn("Recurso no encontrado path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    //Captura errores de solicitud invalida y credenciales incorrectas con HTTP 400.
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        log.warn("Solicitud invalida path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return error(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {       
        
        Map<String, String> validaciones = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> validaciones.put(error.getField(), error.getDefaultMessage()));

        log.warn(
            "Validacion fallida path={} errores={}", 
            request.getRequestURI(), 
            validaciones);

        return error(
            HttpStatus.BAD_REQUEST, 
            "Datos de entrada no validos", 
            request.getRequestURI(), 
            validaciones);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneral(Exception ex, HttpServletRequest request) {
        
        log.error("Error inesperado path={}", request.getRequestURI(), ex);
        
        return error(
            HttpStatus.INTERNAL_SERVER_ERROR, 
            "ERROR INTERNO DEL SERVICIO", 
            request.getRequestURI(), 
            null);
    }

    // Centraliza la construccion de ApiErrorDTO para mantener el mismo formato en todos los errores.
    private ResponseEntity<ApiErrorDTO> error(
        HttpStatus status, 
        String mensaje,
        String path,
        Map<String, String> validaciones
    ){
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
