package com.project.sentimentapi.domain.event;

import java.time.LocalDateTime;

// POJO puro — sin extends ApplicationEvent, sin imports de Spring
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