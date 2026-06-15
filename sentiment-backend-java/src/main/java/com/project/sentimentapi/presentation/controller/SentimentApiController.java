package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SentimentsResponseDto;
import com.project.sentimentapi.service.SentimentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// Controlador movido a presentation/; usa SentimentService ya que no existe
// un port/in dedicado para la llamada directa al modelo (endpoint de diagnóstico)
@RestController
@RequestMapping("/api/sentiment/analyze")
@Validated
@RequiredArgsConstructor
public class SentimentApiController {

    private final SentimentService sentimentService;

    @GetMapping
    public String mensajeDePrueba() {
        return "Sentiment API disponible";
    }

    @PostMapping
    public ResponseEntity<?> analizarTexto(
            @NotBlank(message = "Se ha ingresado un mensaje vacío")
            @Size(min = 5, max = 2000, message = "El texto debe contener entre 5 y 2000 caracteres")
            @RequestBody(required = false) String texto) {

        Optional<ResponseDto> resultado = sentimentService.consultarSentimiento(texto);
        return resultado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build());
    }

    @PostMapping("/batch")
    public ResponseEntity<?> analizarBatch(
            @NotBlank(message = "Se ha ingresado un mensaje vacío")
            @Size(min = 5, max = 20000, message = "El texto debe contener entre 5 y 20000 caracteres")
            @RequestBody(required = false) String texto) {

        Optional<SentimentsResponseDto> resultado = sentimentService.consultarSentimientos(texto);
        return resultado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build());
    }
}
