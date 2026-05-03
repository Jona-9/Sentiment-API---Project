package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SentimentsResponseDto;
import com.project.sentimentapi.service.SentimentService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/sentiment/analyze")
@Validated
public class SentimentApiController {
    SentimentService sentimentService;

    public SentimentApiController(SentimentService sentimentService) {
        this.sentimentService = sentimentService;
    }

    @GetMapping
    public String mensajeDePrueba() {
        return "retornando mensaje de prueba";
    }

    @PostMapping
    public ResponseEntity<?> postTexto(@NotBlank(message = "Se ha ingresado un mensaje vacio")
                                       @Size(min = 5, max = 2000, message = "El texto ingresado debe contener 5 o 2000 carácteres")
                                       @RequestBody(required = false) String texto) {
        Optional<ResponseDto> responseDto = sentimentService.consultarSentimiento(texto);
        if (!responseDto.isEmpty()) {
            return ResponseEntity.ok(responseDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Hubo un error al comunicarse con otro servidor");
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<?> postTextos(@NotBlank(message = "Se ha ingresado un mensaje vacio")
                                        @Size(min = 5, max = 20000, message = "El texto ingresado debe contener 5 o 20000 carácteres")
                                        @RequestBody(required = false) String texto

    ) {
        Optional<SentimentsResponseDto> responseDto = sentimentService.consultarSentimientos(texto);
        if (!responseDto.isEmpty()) {
            return ResponseEntity.ok(responseDto.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Hubo un error al comunicarse con otro servidor");
        }
    }
}
