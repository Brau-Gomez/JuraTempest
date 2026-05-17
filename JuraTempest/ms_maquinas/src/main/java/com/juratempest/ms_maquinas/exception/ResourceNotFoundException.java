package com.juratempest.ms_maquinas.exception;



public class ResourceNotFoundException extends RuntimeException{
    // Representa que una maquina o recurso consultado no fue encontrado.
    // Se usa como RuntimeException para que el manejador global la convierta en una respuesta 404.
    public ResourceNotFoundException(String mess){
        super(mess);
    }
}
