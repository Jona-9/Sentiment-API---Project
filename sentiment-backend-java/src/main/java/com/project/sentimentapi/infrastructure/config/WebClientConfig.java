package com.project.sentimentapi.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

// Patrón SINGLETON: @Bean en Spring tiene scope Singleton por defecto.
// La instancia se crea UNA sola vez al arrancar y se reutiliza en todo el contexto.
// Reemplaza a ConectarApi.java que creaba un WebClient nuevo en cada llamada (costoso).
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient sentimentWebClient(EndPointConfg config) {
        return WebClient.builder()
                .baseUrl(config.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer ->
                    configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)
                )
                .build();
    }
}
