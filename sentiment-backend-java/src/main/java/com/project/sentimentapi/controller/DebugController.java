package com.project.sentimentapi.controller;

import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.CategoriaRepository;
import com.project.sentimentapi.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/debug")
    public class DebugController {

        /**
         * Healthcheck público para Railway
         */
        @GetMapping("/health")
        public ResponseEntity<?> health() {
            Map<String, String> response = new HashMap<>();
            response.put("status", "UP");
            return ResponseEntity.ok(response);
        }

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private CategoriaRepository categoriaRepository;

        /**
         * ⚠️ ENDPOINT TEMPORAL: Crear categorías manualmente
         * Usar solo para usuarios ya registrados que no tienen categorías
         */
        @PostMapping("/crear-categorias")
        public ResponseEntity<?> crearCategoriasManual(HttpServletRequest request) {
            Integer usuarioId = (Integer) request.getAttribute("usuarioId");

            if (usuarioId == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }

            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar si ya tiene categorías
            List<Categoria> categoriasExistentes = categoriaRepository.findByUsuarioOrderByNombreCategoriaAsc(usuario);
            if (!categoriasExistentes.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Ya tienes " + categoriasExistentes.size() + " categorías creadas");
                response.put("total", categoriasExistentes.size());
                return ResponseEntity.ok(response);
            }

            // Crear las 12 categorías por defecto
            List<Categoria> categorias = new ArrayList<>();

            categorias.add(new Categoria("Electrónica", "Productos electrónicos, smartphones, computadoras y accesorios tecnológicos", usuario));
            categorias.add(new Categoria("Ropa y Moda", "Vestimenta, calzado, accesorios y productos de moda", usuario));
            categorias.add(new Categoria("Alimentos y Bebidas", "Productos comestibles, bebidas, snacks y comida preparada", usuario));
            categorias.add(new Categoria("Hogar y Decoración", "Muebles, decoración, artículos para el hogar y jardín", usuario));
            categorias.add(new Categoria("Belleza y Cuidado Personal", "Cosméticos, productos de belleza, cuidado de la piel e higiene personal", usuario));
            categorias.add(new Categoria("Entretenimiento", "Videojuegos, libros, películas, música y hobbies", usuario));
            categorias.add(new Categoria("Deportes y Fitness", "Equipamiento deportivo, ropa deportiva y productos para ejercicio", usuario));
            categorias.add(new Categoria("Servicios", "Servicios profesionales, delivery, suscripciones y servicios digitales", usuario));
            categorias.add(new Categoria("Automotriz", "Vehículos, repuestos, accesorios y servicios para automóviles", usuario));
            categorias.add(new Categoria("Educación", "Cursos, capacitaciones, material educativo y servicios académicos", usuario));
            categorias.add(new Categoria("Salud y Bienestar", "Productos médicos, suplementos, vitaminas y servicios de salud", usuario));
            categorias.add(new Categoria("Niños y Bebés", "Productos para bebés, juguetes, ropa infantil y artículos de maternidad", usuario));

            categoriaRepository.saveAll(categorias);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Categorías creadas exitosamente");
            response.put("total", categorias.size());
            response.put("usuario", usuario.getNombre() + " " + usuario.getApellido());

            return ResponseEntity.ok(response);
        }

        /**
         * Ver información del usuario y sus categorías
         */
        @GetMapping("/info")
        public ResponseEntity<?> info(HttpServletRequest request) {
            Integer usuarioId = (Integer) request.getAttribute("usuarioId");

            if (usuarioId == null) {
                return ResponseEntity.status(401).body("No autorizado");
            }

            User usuario = userRepository.findById(usuarioId).orElse(null);
            if (usuario == null) {
                return ResponseEntity.ok("Usuario no encontrado");
            }

            List<Categoria> categorias = categoriaRepository.findByUsuarioOrderByNombreCategoriaAsc(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("usuarioId", usuario.getUsuarioID());
            response.put("nombre", usuario.getNombre());
            response.put("apellido", usuario.getApellido());
            response.put("correo", usuario.getCorreo());
            response.put("totalCategorias", categorias.size());

            return ResponseEntity.ok(response);
        }
    }