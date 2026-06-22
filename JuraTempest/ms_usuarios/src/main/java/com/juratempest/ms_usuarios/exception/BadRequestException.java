package com.juratempest.ms_usuarios.exception;

public class BadRequestException extends RuntimeException {
    // Representa errores causados por datos incorrectos enviados por el cliente.
    // Extendemos RuntimeException para que Spring pueda capturarla en el manejador global sin obligar try/catch.
    public BadRequestException(String message) {
        super(message);
    }
}
