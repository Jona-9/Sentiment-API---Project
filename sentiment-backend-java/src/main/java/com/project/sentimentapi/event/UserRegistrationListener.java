package com.project.sentimentapi.event;

import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserRegistrationListener {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @EventListener
    @Transactional
    public void handleUserRegistered(UserRegisteredEvent event) {
        User usuario = event.getUsuario();

        System.out.println("====================================");
        System.out.println("✅ EVENTO: Creando categorías para usuario: " + usuario.getCorreo());
        System.out.println("✅ Usuario ID: " + usuario.getUsuarioID());

        try {
            List<Categoria> categoriasDefault = crearCategoriasDefault(usuario);
            categoriaRepository.saveAll(categoriasDefault);

            System.out.println("✅ ÉXITO: " + categoriasDefault.size() + " categorías creadas");
            System.out.println("====================================");
        } catch (Exception e) {
            System.err.println("❌ ERROR al crear categorías: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-lanzar para que Spring maneje la transacción
        }
    }

    /**
     * ✅ Define las 12 categorías por defecto que cada usuario tendrá.
     * Estas aparecerán automáticamente en GET /categoria
     */
    private List<Categoria> crearCategoriasDefault(User usuario) {
        List<Categoria> categorias = new ArrayList<>();

        // 📱 Tecnología y Electrónica
        categorias.add(new Categoria(
                "Electrónica",
                "Productos electrónicos, smartphones, computadoras y accesorios tecnológicos",
                usuario
        ));

        // 👕 Moda y Accesorios
        categorias.add(new Categoria(
                "Ropa y Moda",
                "Vestimenta, calzado, accesorios y productos de moda",
                usuario
        ));

        // 🍕 Alimentos y Bebidas
        categorias.add(new Categoria(
                "Alimentos y Bebidas",
                "Productos comestibles, bebidas, snacks y comida preparada",
                usuario
        ));

        // 🏠 Hogar y Decoración
        categorias.add(new Categoria(
                "Hogar y Decoración",
                "Muebles, decoración, artículos para el hogar y jardín",
                usuario
        ));

        // 💄 Belleza y Cuidado Personal
        categorias.add(new Categoria(
                "Belleza y Cuidado Personal",
                "Cosméticos, productos de belleza, cuidado de la piel e higiene personal",
                usuario
        ));

        // 🎮 Entretenimiento
        categorias.add(new Categoria(
                "Entretenimiento",
                "Videojuegos, libros, películas, música y hobbies",
                usuario
        ));

        // ⚽ Deportes y Fitness
        categorias.add(new Categoria(
                "Deportes y Fitness",
                "Equipamiento deportivo, ropa deportiva y productos para ejercicio",
                usuario
        ));

        // 🛠️ Servicios
        categorias.add(new Categoria(
                "Servicios",
                "Servicios profesionales, delivery, suscripciones y servicios digitales",
                usuario
        ));

        // 🚗 Automotriz
        categorias.add(new Categoria(
                "Automotriz",
                "Vehículos, repuestos, accesorios y servicios para automóviles",
                usuario
        ));

        // 📚 Educación
        categorias.add(new Categoria(
                "Educación",
                "Cursos, capacitaciones, material educativo y servicios académicos",
                usuario
        ));

        // 🏥 Salud y Bienestar
        categorias.add(new Categoria(
                "Salud y Bienestar",
                "Productos médicos, suplementos, vitaminas y servicios de salud",
                usuario
        ));

        // 🧸 Niños y Bebés
        categorias.add(new Categoria(
                "Niños y Bebés",
                "Productos para bebés, juguetes, ropa infantil y artículos de maternidad",
                usuario
        ));

        return categorias;
    }
}
