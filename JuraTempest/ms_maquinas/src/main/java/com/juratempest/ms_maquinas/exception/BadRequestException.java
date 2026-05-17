package com.juratempest.ms_maquinas.exception;

public class BadRequestException extends RuntimeException {
    // Representa errores de reglas de negocio o datos invalidos del cliente.
    // El GlobalExceptionHandler la transforma en una respuesta HTTP 400.
    public BadRequestException(String mensaje){
        super(mensaje);
    }

}