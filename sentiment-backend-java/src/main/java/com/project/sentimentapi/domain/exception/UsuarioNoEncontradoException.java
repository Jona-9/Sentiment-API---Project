package com.project.sentimentapi.domain.exception;

// EXCEPCIÓN DE DOMINIO. Se lanza cuando no existe un usuario con el correo dado
// (ej. login o recuperación). El GlobalExceptionHandler la mapea a HTTP 404.
public class UsuarioNoEncontradoException extends RuntimeException {

    private final String email;

    public UsuarioNoEncontradoException(String email) {
        super("No se encontró usuario con correo: " + email);
        this.email = email;
    }

    public String getEmail() { return email; }
}