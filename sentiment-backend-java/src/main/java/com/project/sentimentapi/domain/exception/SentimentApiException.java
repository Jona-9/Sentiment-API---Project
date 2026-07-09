package com.project.sentimentapi.domain.exception;

public class SentimentApiException extends RuntimeException {

    public SentimentApiException(String mensaje) {
        super(mensaje);
    }

    public SentimentApiException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}