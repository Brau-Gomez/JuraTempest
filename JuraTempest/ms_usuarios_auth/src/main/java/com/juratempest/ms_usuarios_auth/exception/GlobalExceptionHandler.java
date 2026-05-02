package com.juratempest.ms_usuarios_auth.exception;

import com.juratempest.ms_usuarios_auth.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler({BadRequestException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorDTO> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validations = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validations.put(error.getField(), error.getDefaultMessage())
        );
        return buildError(HttpStatus.BAD_REQUEST, "Datos de entrada invalidos", request.getRequestURI(), validations);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex, HttpServletRequest request) {
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
