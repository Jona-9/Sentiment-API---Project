package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.domain.port.in.GestionarCategoriaUseCase;
import com.project.sentimentapi.presentation.dto.response.CategoriaDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// CONTROLLER REST (capa presentation). Patrón Controller (GRASP).
// ISP: CategoriaController solo depende de GestionarCategoriaUseCase.
@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final GestionarCategoriaUseCase categoriaUseCase;

    @PostMapping
    public ResponseEntity<?> crearCategoria(
            HttpServletRequest request,
            @RequestBody Map<String, String> body) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        String nombreCategoria = body.get("nombreCategoria");
        String descripcion = body.get("descripcion");

        try {
            CategoriaDto categoria = categoriaUseCase.crearCategoria(nombreCategoria, descripcion, usuarioId);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDto>> obtenerCategorias(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(categoriaUseCase.obtenerCategoriasPorUsuario(usuarioId));
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<?> obtenerCategoria(
            HttpServletRequest request,
            @PathVariable Integer categoriaId) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            CategoriaDto categoria = categoriaUseCase.obtenerCategoriaPorId(categoriaId, usuarioId);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
