package com.project.sentimentapi.application.event;

import com.project.sentimentapi.domain.event.UserRegisteredEvent;
import com.project.sentimentapi.domain.port.out.EmailPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// Patrón Observer: reacciona al evento de dominio UserRegisteredEvent
// publicado por RegistrarUsuarioUseCaseImpl al completar el registro.
// DIP: depende de EmailPort (abstracción), no de la implementación concreta EmailAdapter.
@Component
@RequiredArgsConstructor
public class UserRegistrationListener {

    private final EmailPort emailPort;

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        emailPort.enviarBienvenida(event.getEmail(), event.getNombre());
    }
}
