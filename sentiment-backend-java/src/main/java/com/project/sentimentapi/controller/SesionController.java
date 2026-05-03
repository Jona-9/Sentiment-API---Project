package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.ComentariosRequestDto;
import com.project.sentimentapi.dto.CsvBatchRequestDto;
import com.project.sentimentapi.dto.SesionDto;
import com.project.sentimentapi.dto.SesionPreviaInfoDto;
import com.project.sentimentapi.service.SesionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/sesion")
public class SesionController {

    @Autowired
    SesionService sesionService;

    @PostMapping
    public ResponseEntity<?> guardarSesion(
            HttpServletRequest request,
            @RequestBody SesionDto sesionDto) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        // ✅ VALIDACIÓN EXPLÍCITA
        if (usuarioId == null) {
            System.err.println("❌ ERROR: usuarioId es NULL en /sesion");
            return ResponseEntity.status(401).body("No autorizado - Token inválido o faltante");
        }

        System.out.println("✅ Usuario autenticado con ID: " + usuarioId);
        sesionService.guardarSesion(sesionDto, usuarioId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<SesionDto>> obtenerSesiones(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            System.err.println("❌ ERROR: usuarioId es NULL en /sesion GET");
            return ResponseEntity.status(401).build();
        }

        List<SesionDto> sesiones = sesionService.obtenerSesionesPorUsuario(usuarioId);
        return ResponseEntity.ok(sesiones);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<SesionDto>> obtenerHistorialSesiones(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            System.err.println("❌ ERROR: usuarioId es NULL en /sesion/historial");
            return ResponseEntity.status(401).build();
        }

        List<SesionDto> sesiones = sesionService.obtenerSesionesPorUsuario(usuarioId);
        return ResponseEntity.ok(sesiones);
    }

    @PostMapping("/analizar")
    public ResponseEntity<?> analizarComentarios(
            HttpServletRequest request,
            @RequestBody ComentariosRequestDto comentariosRequest) {

        // ✅ VALIDACIÓN EXPLÍCITA
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            System.err.println("❌ ERROR: usuarioId es NULL en /sesion/analizar");
            System.err.println("❌ Headers recibidos: " + request.getHeader("Authorization"));
            return ResponseEntity.status(401).body("No autorizado - Token inválido o faltante");
        }

        System.out.println("✅ Analizando comentarios para usuario ID: " + usuarioId);

        try {
            SesionDto sesion = sesionService.analizarYGuardarComentarios(
                    comentariosRequest.getComentarios(),
                    usuarioId
            );

            if (sesion != null) {
                System.out.println("✅ Sesión guardada exitosamente: " + sesion.getSesionId());
                return ResponseEntity.ok(sesion);
            } else {
                System.err.println("❌ ERROR: Servicio retornó null");
                return ResponseEntity.status(502).body("Error al analizar los comentarios");
            }
        } catch (Exception e) {
            System.err.println("❌ EXCEPCIÓN en analizarComentarios:");
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno: " + e.getMessage());
        }
    }
    @PostMapping("/analizar-con-producto")
    public ResponseEntity<?> analizarConProducto(
            HttpServletRequest request, @RequestBody HashMap<String, Object> body
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            @SuppressWarnings("unchecked")
            List<String> comentarios = (List<String>) body.get("comentarios");
            Integer productoId = (Integer) body.get("productoId");

            SesionDto sesion = sesionService.analizarYGuardarConProducto(
                    comentarios,
                    usuarioId,
                    productoId
            );

            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
    @GetMapping("/ultima-sesion-productos")
    public ResponseEntity<?> obtenerProductosUltimaSesion(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            SesionPreviaInfoDto info = sesionService.obtenerProductosUltimaSesion(usuarioId);

            if (info == null) {
                return ResponseEntity.ok(new HashMap<String, String>() {{
                    put("mensaje", "No hay sesiones previas");
                }});
            }

            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * ✨ NUEVO: Analizar con los mismos productos de una sesión anterior
     */
    @PostMapping("/analizar-con-productos-previos")
    public ResponseEntity<?> analizarConProductosPrevios(
            HttpServletRequest request,
            @RequestBody HashMap<String, Object> body
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            @SuppressWarnings("unchecked")
            List<String> comentarios = (List<String>) body.get("comentarios");
            Integer sesionPreviaId = (Integer) body.get("sesionPreviaId");

            SesionDto sesion = sesionService.analizarConMismosProductos(
                    comentarios,
                    usuarioId,
                    sesionPreviaId
            );

            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * ✨ NUEVO: Analizar con productos seleccionados manualmente
     */
    @PostMapping("/analizar-con-lista-productos")
    public ResponseEntity<?> analizarConListaProductos(
            HttpServletRequest request,
            @RequestBody HashMap<String, Object> body
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            @SuppressWarnings("unchecked")
            List<String> comentarios = (List<String>) body.get("comentarios");

            @SuppressWarnings("unchecked")
            List<Integer> productosIds = (List<Integer>) body.get("productosIds");

            SesionDto sesion = sesionService.analizarConMultiplesProductos(
                    comentarios,
                    usuarioId,
                    productosIds
            );

            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * ✨ NUEVO: Analizar batch CSV con auto-creación de categorías y productos
     * Recibe entradas con texto, producto (opcional) y categoría (opcional)
     */
    @PostMapping("/analizar-csv-batch")
    public ResponseEntity<?> analizarCsvBatch(
            HttpServletRequest request,
            @RequestBody CsvBatchRequestDto csvBatchRequest
    ) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            if (csvBatchRequest.getEntradas() == null || csvBatchRequest.getEntradas().isEmpty()) {
                return ResponseEntity.badRequest().body("No hay entradas para analizar");
            }

            SesionDto sesion = sesionService.analizarBatchConCsv(
                    csvBatchRequest.getEntradas(),
                    usuarioId
            );

            return ResponseEntity.ok(sesion);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}