package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.port.in.RecuperarContrasenaUseCase;
import com.project.sentimentapi.domain.port.out.EmailPort;
import com.project.sentimentapi.domain.port.out.UsuarioRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// Principio SRP: solo gestiona recuperación de contraseña.
// Principio DIP: EmailPort es una interfaz — no sabe que hay Resend ni JavaMail detrás.
@Service
public class RecuperarContrasenaUseCaseImpl implements RecuperarContrasenaUseCase {

    private final UsuarioRepositoryPort usuarioPort;
    private final EmailPort emailPort;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public RecuperarContrasenaUseCaseImpl(UsuarioRepositoryPort usuarioPort,
                                          EmailPort emailPort) {
        this.usuarioPort = usuarioPort;
        this.emailPort = emailPort;
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        // Nota: buscamos por email; el adapter mapea correo ↔ email
        usuarioPort.buscarPorEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta con ese correo"));

        // El token lo gestiona el servicio de email adapter (Resend)
        String token = UUID.randomUUID().toString();
        String urlReset = frontendUrl + "/reset-password";

        // Delega el envío al port (OCP: cambiar proveedor de email = nuevo adapter)
        emailPort.enviarResetPassword(email, token, urlReset);
    }

    @Override
    @Transactional
    public void resetPassword(String token, String nuevaContrasena) {
        // La búsqueda por token requiere un método en el adapter;
        // por ahora delegamos la validación al adapter existente via excepción
        throw new UnsupportedOperationException(
                "resetPassword debe implementarse con soporte de token en UsuarioRepositoryPort");
    }
}