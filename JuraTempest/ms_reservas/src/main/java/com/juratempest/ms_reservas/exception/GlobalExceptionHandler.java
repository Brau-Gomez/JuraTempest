package com.juratempest.ms_reservas.exception;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import com.juratempest.ms_reservas.dto.ApiErrorDTO;
import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    // Captura recursos inexistentes, como una reserva que no se encontro.
    // Responde 404 para indicar que el recurso solicitado no existe.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorDTO> manejarNotFound(ResourceNotFoundException ex){
        return error(HttpStatus.NOT_FOUND,
            ex.getMessage(),
            null,
            null);
    }

    // Captura errores de validacion producidos por @Valid.
    // Recorre los errores de campo para poder entregar una respuesta de validacion uniforme.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request){

        Map<String,String> validaciones = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
            .forEach(error ->
                validaciones.put(error.getField(), error.getDefaultMessage()));
            
        return error(HttpStatus.BAD_REQUEST,
            "Datos de entrada no validos",
            request.getRequestURI(),
            validaciones);
    }

    // Captura errores de solicitud incorrecta definidos por reglas de negocio.
    // Se usa para conflictos como intentar reservar una maquina en un horario ocupado.
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorDTO> manejarBadRequest(BadRequestException ex, HttpServletRequest request){
        return error(HttpStatus.BAD_REQUEST,
             ex.getMessage(),
             request.getRequestURI(),
             null 
            );
        }
    //Maneja los errores no previstos.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> manejarGeneric(Exception ex, HttpServletRequest request){
        return error(HttpStatus.INTERNAL_SERVER_ERROR,
             "Error interno del servicio",
             request.getRequestURI(),
             null 
            );
    }

    // Construye el cuerpo comun de error para este microservicio.
    // Centralizarlo evita duplicacion y mantiene respuestas de error con el mismo formato.
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
