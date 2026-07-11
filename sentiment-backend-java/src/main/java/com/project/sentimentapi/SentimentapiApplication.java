package com.project.sentimentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// PUNTO DE ARRANQUE de la aplicación Spring Boot.
// @SpringBootApplication activa autoconfiguración y el escaneo de componentes (@Service,
// @Component, @RestController, @Configuration) en todo el paquete y sus subpaquetes.
// @EnableAsync habilita @Async (usado por EmailAdapter para enviar correos sin bloquear
// la respuesta HTTP). El método main() levanta el servidor embebido.
@SpringBootApplication
@EnableAsync
public class SentimentapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentimentapiApplication.class, args);
	}

}