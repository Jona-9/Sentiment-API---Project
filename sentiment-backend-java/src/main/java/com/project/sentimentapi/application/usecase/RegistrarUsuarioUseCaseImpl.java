package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.event.UserRegisteredEvent;
import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.application.port.in.RegistrarUsuarioUseCase;
import com.project.sentimentapi.domain.port.out.UsuarioRepositoryPort;
import com.project.sentimentapi.application.dto.request.RegistroRequestDto;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Principio SRP: solo registra usuarios — no hace login ni recuperación.
// Principio DIP: depende de UsuarioRepositoryPort (interfaz), no de UserRepository (JPA).
// Patrón Observer: publica UserRegisteredEvent para que el listener envíe el email.
@Service
public class RegistrarUsuarioUseCaseImpl implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioPort;
    private final ApplicationEventPublisher eventPublisher;

    public RegistrarUsuarioUseCaseImpl(UsuarioRepositoryPort usuarioPort,
                                       ApplicationEventPublisher eventPublisher) {
        this.usuarioPort = usuarioPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void registrar(RegistroRequestDto request) {
        // Validar email duplicado
        if (usuarioPort.existePorEmail(request.getCorreo())) {
            throw new RuntimeException("El correo ya esta registrado");
        }

        // Hashear contraseña (lógica de negocio pura — sin JPA)
        String passwordHash = BCrypt.hashpw(request.getContrasena(), BCrypt.gensalt());

        // Crear modelo de dominio (POJO puro, sin @Entity)
        Usuario nuevoUsuario = new Usuario(
                request.getNombre(),
                request.getApellido(),
                request.getCorreo(),
                passwordHash
        );

        // Guardar a través del port (no sabe si hay MySQL, MongoDB, etc.)
        Usuario guardado = usuarioPort.guardar(nuevoUsuario);

        // Publicar evento → UserRegistrationListener enviará el email (Patrón Observer)
        eventPublisher.publishEvent(new UserRegisteredEvent(guardado.getEmail(), guardado.getNombre()));
    }
}