package com.juratempest.ms_reservas.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String mensaje){
        super(mensaje);
    }
}
