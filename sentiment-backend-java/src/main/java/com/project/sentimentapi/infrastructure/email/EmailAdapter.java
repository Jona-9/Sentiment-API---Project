package com.project.sentimentapi.infrastructure.email;

import com.project.sentimentapi.domain.port.out.EmailPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

// Patrón ADAPTER: implementa EmailPort (dominio) usando la API de Resend
// El dominio solo conoce EmailPort — nunca sabe que existe Resend ni WebClient
@Component
public class EmailAdapter implements EmailPort {

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Async
    @Override
    public void enviarBienvenida(String destinatario, String nombre) {
        try {
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>"
                    + "<h2 style='color: #333;'>¡Bienvenido a Sentiment API, " + nombre + "!</h2>"
                    + "<p>Tu cuenta ha sido creada exitosamente.</p>"
                    + "<p>Ya puedes iniciar sesión y comenzar a analizar sentimientos.</p>"
                    + "</div>";

            enviarViaResend(destinatario, "¡Bienvenido a Sentiment API!", htmlContent);
        } catch (Exception e) {
            System.err.println("Error al enviar email de bienvenida: " + e.getMessage());
        }
    }

    @Async
    @Override
    public void enviarResetPassword(String destinatario, String token, String urlReset) {
        try {
            String resetLink = urlReset + "?token=" + token;
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>"
                    + "<h2 style='color: #333;'>Recuperación de contraseña</h2>"
                    + "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>"
                    + "<a href='" + resetLink + "' style='"
                    + "background-color: #4CAF50; color: white; padding: 12px 24px;"
                    + "text-decoration: none; border-radius: 4px; display: inline-block;'>"
                    + "Restablecer contraseña"
                    + "</a>"
                    + "<p style='color: #888; margin-top: 20px;'>Este enlace expira en 30 minutos.</p>"
                    + "</div>";

            enviarViaResend(destinatario, "Recuperación de contraseña - SentimentAPI", htmlContent);
        } catch (Exception e) {
            System.err.println("Error al enviar email de recuperación: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación");
        }
    }

    // Método privado que concentra la llamada HTTP real a Resend (SRP dentro del adapter).
    // Es el único punto que conoce el formato de la API externa; si se cambia de proveedor
    // solo cambia este método (o se crea otro adapter que implemente EmailPort).
    private void enviarViaResend(String destinatario, String asunto, String htmlContent) {
        Map<String, Object> body = Map.of(
                "from", "onboarding@resend.dev",
                "to", List.of(destinatario),
                "subject", asunto,
                "html", htmlContent
        );

        WebClient resendClient = WebClient.create("https://api.resend.com");
        resendClient.post()
                .uri("/emails")
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
