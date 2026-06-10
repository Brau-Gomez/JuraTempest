package com.juratempest.ms_notificaciones.exception;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        // TODO: Llamar al constructor padre RuntimeException con el mensaje recibido.
        super(message);
    }
}
