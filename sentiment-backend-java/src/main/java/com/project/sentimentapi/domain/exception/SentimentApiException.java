package com.project.sentimentapi.domain.exception;

// EXCEPCIÓN DE DOMINIO. Señala una falla al comunicarse con la API de sentimientos
// (servicio caído, timeout, respuesta inválida). El GlobalExceptionHandler la
// traduce a un código HTTP adecuado. Vive en domain para no acoplar la lógica a
// excepciones de Spring/HTTP.
public class SentimentApiException extends RuntimeException {

    public SentimentApiException(String mensaje) {
        super(mensaje);
    }

    public SentimentApiException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}