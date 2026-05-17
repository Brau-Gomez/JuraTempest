package com.juratempest.ms_reservas.exception;

public class ResourceNotFoundException extends RuntimeException {
    // Representa una busqueda sin resultados o una referencia externa inexistente.
    // Se traduce a HTTP 404 desde el manejador global de excepciones.
    public ResourceNotFoundException(String mensaje){
        super(mensaje);
    }
}
