package com.juratempest.ms_notificaciones.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        // TODO: Llamar al constructor padre RuntimeException con el mensaje recibido.
        super(message);
    }
}
