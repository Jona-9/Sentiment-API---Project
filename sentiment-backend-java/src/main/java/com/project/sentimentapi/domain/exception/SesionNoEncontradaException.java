package com.project.sentimentapi.domain.exception;

public class SesionNoEncontradaException extends RuntimeException {

    private final Integer sesionId;

    public SesionNoEncontradaException(Integer sesionId) {
        super("Sesión no encontrada con id: " + sesionId);
        this.sesionId = sesionId;
    }

    public Integer getSesionId() { return sesionId; }
}
