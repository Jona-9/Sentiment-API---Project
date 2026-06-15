package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.AnalizarCsvUseCase;
import com.project.sentimentapi.dto.CsvAnalysisResponseDto;
import com.project.sentimentapi.dto.CsvEntradaDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ISP: solo inyecta AnalizarCsvUseCase, no una interfaz genérica de análisis
@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvAnalysisController {

    private final AnalizarCsvUseCase analizarUseCase;

    @PostMapping("/analizar")
    public ResponseEntity<?> analizarCsv(
            @RequestBody List<CsvEntradaDto> filas,
            HttpServletRequest request) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            CsvAnalysisResponseDto resultado = analizarUseCase.analizar(filas, usuarioId);
            return ResponseEntity.ok(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Error al analizar CSV: " + e.getMessage());
        }
    }
}
