package com.juratempest.ms_horarios.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String mensaje){
        super(mensaje);
    }

}
