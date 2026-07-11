package com.project.sentimentapi.domain.exception;

// EXCEPCIÓN DE DOMINIO. Se lanza en el reset de contraseña cuando el token es
// inválido o ya venció. El GlobalExceptionHandler la traduce a un HTTP de error.
public class TokenExpiradoException extends RuntimeException {

    public TokenExpiradoException() {
        super("El token de recuperación ha expirado o es inválido");
    }
}
