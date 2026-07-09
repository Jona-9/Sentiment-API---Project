package com.project.sentimentapi.presentation.controller;

import com.project.sentimentapi.infrastructure.persistence.entity.CategoriaJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.CategoriaJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// DebugController movido a presentation/; mantiene referencias legacy temporalmente.
// Pendiente de migración: reemplazar UserRepository/CategoriaRepository por use cases de dominio.
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final UsuarioJpaRepository userRepository;
    private final CategoriaJpaRepository categoriaRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    @PostMapping("/crear-categorias")
    public ResponseEntity<?> crearCategoriasManual(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        UsuarioJpaEntity usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<CategoriaJpaEntity> categoriasExistentes =
                categoriaRepository.findByUsuarioOrderByNombreCategoriaAsc(usuario);

        if (!categoriasExistentes.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Ya tienes " + categoriasExistentes.size() + " categorías creadas",
                    "total", categoriasExistentes.size()
            ));
        }

        List<CategoriaJpaEntity> categorias = new ArrayList<>();
        categorias.add(new CategoriaJpaEntity("Electrónica", "Productos electrónicos, smartphones, computadoras y accesorios tecnológicos", usuario));
        categorias.add(new CategoriaJpaEntity("Ropa y Moda", "Vestimenta, calzado, accesorios y productos de moda", usuario));
        categorias.add(new CategoriaJpaEntity("Alimentos y Bebidas", "Productos comestibles, bebidas, snacks y comida preparada", usuario));
        categorias.add(new CategoriaJpaEntity("Hogar y Decoración", "Muebles, decoración, artículos para el hogar y jardín", usuario));
        categorias.add(new CategoriaJpaEntity("Belleza y Cuidado Personal", "Cosméticos, productos de belleza, cuidado de la piel e higiene personal", usuario));
        categorias.add(new CategoriaJpaEntity("Entretenimiento", "Videojuegos, libros, películas, música y hobbies", usuario));
        categorias.add(new CategoriaJpaEntity("Deportes y Fitness", "Equipamiento deportivo, ropa deportiva y productos para ejercicio", usuario));
        categorias.add(new CategoriaJpaEntity("Servicios", "Servicios profesionales, delivery, suscripciones y servicios digitales", usuario));
        categorias.add(new CategoriaJpaEntity("Automotriz", "Vehículos, repuestos, accesorios y servicios para automóviles", usuario));
        categorias.add(new CategoriaJpaEntity("Educación", "Cursos, capacitaciones, material educativo y servicios académicos", usuario));
        categorias.add(new CategoriaJpaEntity("Salud y Bienestar", "Productos médicos, suplementos, vitaminas y servicios de salud", usuario));
        categorias.add(new CategoriaJpaEntity("Niños y Bebés", "Productos para bebés, juguetes, ropa infantil y artículos de maternidad", usuario));

        categoriaRepository.saveAll(categorias);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Categorías creadas exitosamente",
                "total", categorias.size(),
                "usuario", usuario.getNombre() + " " + usuario.getApellido()
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<?> info(HttpServletRequest request) {
        Integer usuarioId = (Integer) request.getAttribute("usuarioId");
        if (usuarioId == null) {
            return ResponseEntity.status(401).body("No autorizado");
        }

        UsuarioJpaEntity usuario = userRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return ResponseEntity.ok("Usuario no encontrado");
        }

        List<CategoriaJpaEntity> categorias =
                categoriaRepository.findByUsuarioOrderByNombreCategoriaAsc(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("usuarioId", usuario.getUsuarioID());
        response.put("nombre", usuario.getNombre());
        response.put("apellido", usuario.getApellido());
        response.put("correo", usuario.getCorreo());
        response.put("totalCategorias", categorias.size());

        return ResponseEntity.ok(response);
    }
}