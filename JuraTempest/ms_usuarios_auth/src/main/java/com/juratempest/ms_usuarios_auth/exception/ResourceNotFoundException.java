package com.juratempest.ms_usuarios_auth.exception;

public class ResourceNotFoundException extends RuntimeException {
    // Representa una busqueda sin resultados, como un usuario que no existe.
    // La manejamos como RuntimeException para que el GlobalExceptionHandler la traduzca a HTTP 404.
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
