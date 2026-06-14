package com.project.sentimentapi.domain.port.out;

public interface EmailPort {
    void enviarBienvenida(String destinatario, String nombre);
    void enviarResetPassword(String destinatario, String token, String urlReset);
}