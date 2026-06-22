package com.juratempest.ms_auth.exception;

import com.juratempest.ms_auth.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validations = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validations.put(error.getField(), error.getDefaultMessage())
        );
        return buildError(HttpStatus.BAD_REQUEST, "Datos de entrada invalidos", request.getRequestURI(), validations);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> handleBadRequest(BadRequestException ex, HttpServletRequest request) {
        log.warn("Solicitud invalida path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiErrorDTO> handleUsuarioServiceError(
        WebClientResponseException ex,
        HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        HttpStatus responseStatus = status == null ? HttpStatus.BAD_GATEWAY : status;
        log.warn("Error desde ms-usuarios path={} status={} body={}",
            request.getRequestURI(), ex.getStatusCode().value(), ex.getResponseBodyAsString());
        return buildError(responseStatus, "No se pudo completar la autenticacion", request.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado path={}", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servicio", request.getRequestURI(), null);
    }

    private ResponseEntity<ApiErrorDTO> buildError(
        HttpStatus status,
        String message,
        String path,
        Map<String, String> validations
    ) {
        ApiErrorDTO body = ApiErrorDTO.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .mensaje(message)
            .path(path)
            .validaciones(validations)
            .build();
        return ResponseEntity.status(status).body(body);
    }
}
