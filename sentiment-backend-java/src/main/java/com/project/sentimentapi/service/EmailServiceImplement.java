package com.project.sentimentapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImplement implements EmailService {

    @Value("${resend.api-key}")
    private String resendApiKey;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Async
    @Override
    public void sendRecoveryEmail(String to, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto;'>"
                    + "<h2 style='color: #333;'>Recuperación de contraseña</h2>"
                    + "<p>Hemos recibido una solicitud para restablecer tu contraseña.</p>"
                    + "<p>Haz clic en el siguiente botón para crear una nueva contraseña:</p>"
                    + "<a href='" + resetLink + "' style='"
                    + "background-color: #4CAF50; color: white; padding: 12px 24px;"
                    + "text-decoration: none; border-radius: 4px; display: inline-block;'>"
                    + "Restablecer contraseña"
                    + "</a>"
                    + "<p style='color: #888; margin-top: 20px;'>Este enlace expira en 30 minutos.</p>"
                    + "<p style='color: #888;'>Si no solicitaste este cambio, ignora este correo.</p>"
                    + "</div>";

            Map<String, Object> body = Map.of(
                    "from", "onboarding@resend.dev",
                    "to", List.of(to),
                    "subject", "Recuperación de contraseña - SentimentAPI",
                    "html", htmlContent
            );

            WebClient client = WebClient.create("https://api.resend.com");
            String response = client.post()
                    .uri("/emails")
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("✅ Email de recuperación enviado a: " + to + " | Resend response: " + response);

        } catch (Exception e) {
            System.err.println("❌ Error al enviar email via Resend: " + e.getMessage());
            throw new RuntimeException("Error al enviar el correo de recuperación");
        }
    }
}
