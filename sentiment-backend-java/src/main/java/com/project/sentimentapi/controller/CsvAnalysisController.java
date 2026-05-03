package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.CsvUploadRequestDto;
import com.project.sentimentapi.dto.CsvAnalysisResponseDto;
import com.project.sentimentapi.dto.SesionDto;
import com.project.sentimentapi.service.CsvAnalysisService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/csv")
public class CsvAnalysisController {

    @Autowired
    private CsvAnalysisService csvAnalysisService;

    /**
     * Procesar CSV: crea categorias/productos y analiza comentarios
     * POST /project/api/v2/csv/analizar
     */
    @PostMapping("/analizar")
    public ResponseEntity<?> analizarCsv(
            HttpServletRequest request,
            @RequestBody CsvUploadRequestDto csvData
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado - Token invalido o faltante");
        }

        try {
            CsvAnalysisResponseDto resultado = csvAnalysisService.procesarYAnalizarCsv(
                    csvData.getRows(),
                    usuarioId
            );
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al procesar CSV: " + e.getMessage());
        }
    }

    /**
     * Obtener comparativa de productos del usuario
     * GET /project/api/v2/csv/comparativa-productos
     */
    @GetMapping("/comparativa-productos")
    public ResponseEntity<?> obtenerComparativaProductos(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            var comparativa = csvAnalysisService.obtenerComparativaProductos(usuarioId);
            return ResponseEntity.ok(comparativa);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Obtener comparativa de categorias del usuario
     * GET /project/api/v2/csv/comparativa-categorias
     */
    @GetMapping("/comparativa-categorias")
    public ResponseEntity<?> obtenerComparativaCategorias(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            var comparativa = csvAnalysisService.obtenerComparativaCategorias(usuarioId);
            return ResponseEntity.ok(comparativa);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
