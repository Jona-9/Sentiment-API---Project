package com.project.sentimentapi.domain.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    private final String email;

    public UsuarioNoEncontradoException(String email) {
        super("No se encontró usuario con correo: " + email);
        this.email = email;
    }

    public String getEmail() { return email; }
}