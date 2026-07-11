package com.project.sentimentapi.presentation.exception;

import com.project.sentimentapi.domain.exception.SentimentApiException;
import com.project.sentimentapi.domain.exception.SesionNoEncontradaException;
import com.project.sentimentapi.domain.exception.TokenExpiradoException;
import com.project.sentimentapi.domain.exception.UsuarioNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

// MANEJADOR GLOBAL DE EXCEPCIONES (capa presentation).
// @ControllerAdvice intercepta las excepciones lanzadas por CUALQUIER controller y las
// traduce a una respuesta HTTP uniforme (JSON con timestamp/status/error/message). Así
// los use cases lanzan excepciones de dominio limpias y aquí —y solo aquí— se decide el
// código HTTP: 404 no encontrado, 503 servicio de IA caído, 401 token, 400 argumento, 500 resto.
// LSP: cada handler trabaja con el tipo más específico de la jerarquía de excepciones.
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Usuario no encontrado",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(SesionNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleSesionNoEncontrada(SesionNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 404,
                "error", "Sesión no encontrada",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(SentimentApiException.class)
    public ResponseEntity<Map<String, Object>> handleSentimentApi(SentimentApiException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 503,
                "error", "Servicio de análisis no disponible",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(TokenExpiradoException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpirado(TokenExpiradoException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 401,
                "error", "Token expirado",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 400,
                "error", "Argumento inválido",
                "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", 500,
                "error", "Error interno del servidor",
                "message", "Ocurrió un error inesperado. Contacte al soporte."
        ));
    }
}
