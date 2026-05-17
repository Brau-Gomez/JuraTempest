package com.juratempest.ms_horarios.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Representa una busqueda sin resultados, por ejemplo un bloque horario inexistente.
    // El handler global la convierte en una respuesta HTTP 404.
    public ResourceNotFoundException(String message) {
        super(message);
    }

}
