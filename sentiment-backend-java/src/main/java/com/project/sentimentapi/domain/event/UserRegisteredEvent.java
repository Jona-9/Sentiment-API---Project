package com.project.sentimentapi.domain.event;

import java.time.LocalDateTime;

// EVENTO DE DOMINIO (patrón Observer — capa domain).
// POJO puro: NO extiende ApplicationEvent ni importa nada de Spring, para respetar
// la regla de dependencias (el dominio no conoce el framework).
// Es el "mensaje" que se publica cuando un usuario se registra; el observador
// (UserRegistrationListener) reacciona enviando el correo de bienvenida.
// Inmutable (campos final): una vez ocurrido el hecho, no debe cambiar.
public class UserRegisteredEvent {

    private final String email;
    private final String nombre;
    private final LocalDateTime ocurrioEn;

    public UserRegisteredEvent(String email, String nombre) {
        this.email = email;
        this.nombre = nombre;
        this.ocurrioEn = LocalDateTime.now();
    }

    public String getEmail() { return email; }
    public String getNombre() { return nombre; }
    public LocalDateTime getOcurrioEn() { return ocurrioEn; }
}