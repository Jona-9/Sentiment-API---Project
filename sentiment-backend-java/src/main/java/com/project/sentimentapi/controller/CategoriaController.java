package com.project.sentimentapi.controller;

import com.project.sentimentapi.dto.CategoriaDto;
import com.project.sentimentapi.service.CategoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/categoria")
    public class CategoriaController {

        @Autowired
        private CategoriaService categoriaService;

        @PostMapping
        public ResponseEntity<?> crearCategoria(
                HttpServletRequest request,
                @RequestBody Map<String, String> body
        ) {
            Integer usuarioId = (Integer) request.getAttribute("usuarioId");

            if (usuarioId == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }

            String nombreCategoria = body.get("nombreCategoria");
            String descripcion = body.get("descripcion");

            try {
                CategoriaDto categoria = categoriaService.crearCategoria(nombreCategoria, descripcion, usuarioId);
                return ResponseEntity.ok(categoria);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }

        @GetMapping
        public ResponseEntity<?> obtenerCategorias(HttpServletRequest request) {
            Integer usuarioId = (Integer) request.getAttribute("usuarioId");

            if (usuarioId == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }

            List<CategoriaDto> categorias = categoriaService.obtenerCategoriasPorUsuario(usuarioId);
            return ResponseEntity.ok(categorias);
        }

        @GetMapping("/{categoriaId}")
        public ResponseEntity<?> obtenerCategoriaPorId(
                HttpServletRequest request,
                @PathVariable Integer categoriaId
        ) {
            Integer usuarioId = (Integer) request.getAttribute("usuarioId");

            if (usuarioId == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }

            try {
                CategoriaDto categoria = categoriaService.obtenerCategoriaPorId(categoriaId, usuarioId);
                return ResponseEntity.ok(categoria);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }
