package com.project.sentimentapi.infrastructure.external;

import com.project.sentimentapi.domain.exception.SentimentApiException;
import com.project.sentimentapi.domain.port.out.SentimentAnalysisPort;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// Patrón ADAPTER: convierte SentimentAnalysisPort (dominio) a llamadas reales a la API Python
// Patrón SINGLETON: el WebClient se inyecta como @Bean (instancia única gestionada por Spring)
// Principio OCP: para cambiar de proveedor de IA, solo se crea un nuevo Adapter aquí — el use case no se toca
@Component
public class SentimentApiAdapter implements SentimentAnalysisPort {

    // @Bean singleton definido en WebClientConfig — una sola instancia en todo el contexto Spring
    private final WebClient webClient;

    public SentimentApiAdapter(WebClient sentimentWebClient) {
        this.webClient = sentimentWebClient;
    }

    @Override
    public Optional<SentimentsResponseDto> analizarLote(List<String> textos) {
        try {
            SentimentsResponseDto respuesta = webClient.post()
                    .uri("/predict/batch")
                    .bodyValue(Map.of("texts", textos))
                    .retrieve()
                    .bodyToMono(SentimentsResponseDto.class)
                    .block();
            return Optional.ofNullable(respuesta);
        } catch (WebClientRequestException e) {
            // La API Python no está disponible (sin conexión, timeout, etc.)
            throw new SentimentApiException(
                    "No se pudo conectar con la API de sentimientos: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new SentimentApiException(
                    "Error inesperado al llamar la API de sentimientos: " + e.getMessage(), e);
        }
    }
}