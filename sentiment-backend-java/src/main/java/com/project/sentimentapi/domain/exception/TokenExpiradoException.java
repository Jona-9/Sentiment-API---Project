package com.project.sentimentapi.domain.exception;

public class TokenExpiradoException extends RuntimeException {

    public TokenExpiradoException() {
        super("El token de recuperación ha expirado o es inválido");
    }
}
