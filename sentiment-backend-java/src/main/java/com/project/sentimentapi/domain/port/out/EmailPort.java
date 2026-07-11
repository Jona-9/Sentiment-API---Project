package com.project.sentimentapi.domain.port.out;

// PORT OUT (puerto de salida — capa domain). Contrato de envío de correos que el
// dominio necesita, sin conocer el proveedor (SMTP, SendGrid, etc.). Lo implementa
// EmailAdapter. El listener del patrón Observer depende de este port, no de la
// implementación concreta (DIP).
public interface EmailPort {
    void enviarBienvenida(String destinatario, String nombre);
    void enviarResetPassword(String destinatario, String token, String urlReset);
}