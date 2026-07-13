package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.application.port.in.GestionarProductoUseCase;
import com.project.sentimentapi.application.dto.response.ProductoDto;
import com.project.sentimentapi.application.dto.request.ProductoRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CONTROLLER REST (capa presentation). Patrón Controller (GRASP).
// ISP: ProductoController solo depende de GestionarProductoUseCase.
@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final GestionarProductoUseCase productoUseCase;

    @PostMapping
    public ResponseEntity<?> crearProducto(
            HttpServletRequest request,
            @RequestBody ProductoRequestDto dto) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            ProductoDto producto = productoUseCase.crearProducto(dto, usuarioId);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductoDto>> obtenerProductos(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(productoUseCase.obtenerProductosPorUsuario(usuarioId));
    }

    @GetMapping("/por-categoria")
    public ResponseEntity<?> obtenerPorCategoria(
            HttpServletRequest request,
            @RequestParam Integer categoriaId) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            List<ProductoDto> productos = productoUseCase.obtenerProductosPorCategoria(categoriaId, usuarioId);
            return ResponseEntity.ok(productos);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{productoId}")
    public ResponseEntity<?> obtenerProducto(
            HttpServletRequest request,
            @PathVariable Integer productoId) {

        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        try {
            ProductoDto producto = productoUseCase.obtenerProductoPorId(productoId, usuarioId);
            return ResponseEntity.ok(producto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}