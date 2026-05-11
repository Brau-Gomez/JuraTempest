package com.juratempest.ms_fidelizacion.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String mensaje){
        super(mensaje);
    }
}
