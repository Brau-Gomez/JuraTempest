package com.juratempest.ms_fidelizacion.exception;

public class ResourceNotFoundException extends RuntimeException {
    // Representa una busqueda sin resultados o una referencia externa inexistente.
    // La excepcion se traduce a HTTP 404 desde el GlobalExceptionHandler.
    public ResourceNotFoundException(String mensaje){
        super(mensaje);
    }
}
