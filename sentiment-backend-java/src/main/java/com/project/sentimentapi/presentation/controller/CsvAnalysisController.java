package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.application.port.in.AnalizarCsvUseCase;
import com.project.sentimentapi.application.dto.response.CsvAnalysisResponseDto;
import com.project.sentimentapi.application.dto.request.CsvEntradaDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CONTROLLER REST (capa presentation). Patrón Controller (GRASP): recibe el CSV del
// dashboard y delega toda la orquestación a un único método del use case (fachada). El
// controller no sabe cómo se llama la API de ML, cómo se calculan estadísticas ni cómo
// se guarda la sesión — solo coordina HTTP. DIP: depende del port AnalizarCsvUseCase.
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