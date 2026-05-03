package com.project.sentimentapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SentimentapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SentimentapiApplication.class, args);
	}

}