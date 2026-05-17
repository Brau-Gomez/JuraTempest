package com.juratempest.ms_reservas.exception;

public class BadRequestException extends RuntimeException {
    // Representa errores de negocio causados por datos invalidos del cliente.
    // El GlobalExceptionHandler la convierte en una respuesta HTTP 400.
    public BadRequestException(String mensaje){
        super(mensaje);
    }
}
