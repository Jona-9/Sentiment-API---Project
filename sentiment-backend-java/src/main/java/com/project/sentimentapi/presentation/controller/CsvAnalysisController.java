package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.AnalizarCsvUseCase;
import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto;
import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/csv")
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
            // LOG TEMPORAL para diagnosticar — ver stack trace completo en consola de IntelliJ
            System.err.println("=== ERROR en /csv/analizar ===");
            System.err.println("Mensaje: " + e.getMessage());
            System.err.println("Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "ninguna"));
            e.printStackTrace();
            System.err.println("==============================");
            return ResponseEntity.status(500).body("Error al analizar CSV: " + e.getMessage());
        }
    }
}