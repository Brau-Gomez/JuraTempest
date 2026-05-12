package com.juratempest.ms_reservas.exception;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> manejarNotFound(ResourceNotFoundException ex){

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(error(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> manejarValidacion(MethodArgumentNotValidException ex){

        Map<String,String> validaciones = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
            .forEach(error ->
                validaciones.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest()
            .body(error(HttpStatus.BAD_REQUEST, "DATOS INVALIDOS"));
    }

    private Map<String, Object> error(HttpStatus status, String mensaje){
        Map<String,Object> body = new HashMap<>();
        body.put("timestap",LocalDateTime.now());
        body.put("status",status.value());
        body.put("error",status.getReasonPhrase());
        body.put("mensaje",mensaje);
        body.put("succes",false);
        return body;

    }

}
