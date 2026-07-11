package com.project.sentimentapi.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertSame;

// PRUEBA DEL PATRÓN SINGLETON (Unidad 2 del sílabo — patrones creacionales).
// Demuestra "matemáticamente" que el @Bean del WebClient devuelve SIEMPRE la MISMA
// instancia dentro del contexto de Spring (scope Singleton por defecto), a diferencia del
// código anterior (ConectarApi.client()) que creaba un WebClient nuevo en cada llamada.
class WebClientConfigTest {

    @Test
    @DisplayName("El @Bean del WebClient es Singleton: dos getBean devuelven la misma instancia")
    void webClientBeanEsSingleton() {
        // Arrange — levantar un contexto mínimo con la configuración real WebClientConfig
        try (AnnotationConfigApplicationContext ctx =
                     new AnnotationConfigApplicationContext(WebClientConfig.class, TestEndpointConfig.class)) {

            // Act — pedir el bean dos veces al contenedor
            WebClient primera = ctx.getBean(WebClient.class);
            WebClient segunda = ctx.getBean(WebClient.class);

            // Assert — assertSame compara identidad de referencia (mismo objeto en memoria)
            assertSame(primera, segunda,
                    "Fallo: el WebClient no es Singleton, Spring creó más de una instancia");
        }
    }

    // Provee un EndPointConfg con una URL válida para que WebClientConfig pueda construir
    // el WebClient sin depender del application.properties real.
    @Configuration
    static class TestEndpointConfig {
        @Bean
        EndPointConfg endPointConfg() {
            EndPointConfg config = new EndPointConfg();
            config.setUrl("http://localhost:9999");
            return config;
        }
    }
}
