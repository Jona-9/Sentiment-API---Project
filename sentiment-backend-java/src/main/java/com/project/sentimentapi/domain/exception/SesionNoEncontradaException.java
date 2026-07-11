package com.project.sentimentapi.domain.exception;

// EXCEPCIÓN DE DOMINIO. Se lanza al pedir una sesión inexistente; el
// GlobalExceptionHandler la mapea a HTTP 404. Guarda el id buscado para el mensaje.
public class SesionNoEncontradaException extends RuntimeException {

    private final Integer sesionId;

    public SesionNoEncontradaException(Integer sesionId) {
        super("Sesión no encontrada con id: " + sesionId);
        this.sesionId = sesionId;
    }

    public Integer getSesionId() { return sesionId; }
}
