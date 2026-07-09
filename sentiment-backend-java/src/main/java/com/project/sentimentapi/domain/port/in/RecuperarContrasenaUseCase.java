package com.project.sentimentapi.domain.port.in;

public interface RecuperarContrasenaUseCase {
    void forgotPassword(String email);
    void resetPassword(String token, String nuevaContrasena);
}