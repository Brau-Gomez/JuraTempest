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
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // Captura errores de recursos inexistentes y responde con HTTP 404.
    // Centralizamos esta respuesta para que todos los controladores mantengan el mismo formato de error.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), null);
    }

    // Captura errores de solicitud invalida y credenciales incorrectas con HTTP 400.
    // Agrupar estas excepciones evita repetir codigo cuando el problema viene desde la entrada del cliente.
    @ExceptionHandler({BadRequestException.class, BadCredentialsException.class})
    public ResponseEntity<ApiErrorDTO> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
        log.warn("Solicitud invalida path={} mensaje={}", request.getRequestURI(), ex.getMessage());
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), null);
    }

    // Captura errores generados por validaciones de @Valid en los DTO.
    // Recorremos los campos invalidos para devolver mensajes especificos que ayudan a corregir el formulario.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> validations = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validations.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Validacion fallida path={} errores={}", request.getRequestURI(), validations);
        return buildError(HttpStatus.BAD_REQUEST, "Datos de entrada invalidos", request.getRequestURI(), validations);
    }

    // Captura cualquier excepcion no prevista y responde con HTTP 500.
    // Esto evita filtrar detalles internos del sistema hacia el cliente.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado path={}", request.getRequestURI(), ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servicio", request.getRequestURI(), null);
    }

    // Construye el cuerpo estandar de error usado por todos los handlers anteriores.
    // Tener este metodo privado reduce duplicacion y mantiene un formato uniforme de respuesta.
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
