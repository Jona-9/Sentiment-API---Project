package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.AnalizarTextoUseCase;
import com.project.sentimentapi.presentation.dto.response.ResponseDto;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

// CONTROLLER REST (capa presentation). Patrón Controller (GRASP). Endpoint de análisis
// de texto suelto (diagnóstico/pruebas). @Validated + @NotBlank/@Size validan la entrada
// antes de llegar al use case. Delega en AnalizarTextoUseCase (DIP).
@RestController
@RequestMapping("/sentiment/analyze")
@Validated
@RequiredArgsConstructor
public class SentimentApiController {

    private final AnalizarTextoUseCase analizarTextoUseCase;

    @GetMapping
    public String mensajeDePrueba() {
        return "Sentiment API disponible";
    }

    // Se especifica consumes = "text/plain" para evitar el error de parsing JSON
    @PostMapping(consumes = "text/plain")
    public ResponseEntity<?> analizarTexto(
            @NotBlank(message = "Se ha ingresado un mensaje vacío")
            @Size(min = 5, max = 2000, message = "El texto debe contener entre 5 y 2000 caracteres")
            @RequestBody String texto) {

        Optional<ResponseDto> resultado = analizarTextoUseCase.analizarTexto(texto);
        return resultado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build());
    }

    // Se especifica consumes = "text/plain" para el batch
    @PostMapping(value = "/batch", consumes = "text/plain")
    public ResponseEntity<?> analizarBatch(
            @NotBlank(message = "Se ha ingresado un mensaje vacío")
            @Size(min = 5, max = 20000, message = "El texto debe contener entre 5 y 20000 caracteres")
            @RequestBody String texto) {

        Optional<SentimentsResponseDto> resultado = analizarTextoUseCase.analizarBatch(texto);
        return resultado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.BAD_GATEWAY).build());
    }
}