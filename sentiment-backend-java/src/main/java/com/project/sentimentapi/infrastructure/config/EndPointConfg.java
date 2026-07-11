package com.project.sentimentapi.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

// CONFIGURACIÓN (capa infrastructure). Lee del application.properties las claves con
// prefijo "config" (config.url = URL de la API Python de ML) y las expone como bean.
// @Data (Lombok) genera getters/setters; WebClientConfig usa getUrl() para armar el WebClient.
// Externalizar la URL evita hardcodearla → se cambia el endpoint sin recompilar (OCP).
@Data
@Configuration
@ConfigurationProperties(prefix = "config")
public class EndPointConfg {
    private String url; // config.url en application.properties → base URL del servicio de sentimientos
}
