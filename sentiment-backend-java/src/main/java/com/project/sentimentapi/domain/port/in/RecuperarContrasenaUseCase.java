package com.project.sentimentapi.domain.port.in;

// PORT IN (puerto de entrada — capa domain). Contrato del flujo de recuperación de
// contraseña en dos pasos: forgotPassword genera y envía el token; resetPassword
// valida el token y guarda la nueva contraseña. Barrera DIP.
public interface RecuperarContrasenaUseCase {
    void forgotPassword(String email);
    void resetPassword(String token, String nuevaContrasena);
}