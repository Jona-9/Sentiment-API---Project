package com.project.sentimentapi.infrastructure.config;

import com.project.sentimentapi.infrastructure.persistence.entity.RolJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.RolJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// CONFIGURACIÓN / SEED DE DATOS (capa infrastructure).
// Implementa CommandLineRunner: Spring ejecuta run(...) una vez al arrancar la app.
// Objetivo: garantizar que existan los roles base (ADMIN, USER) y usuarios de prueba
// con contraseña ya hasheada en BCrypt, para que el login funcione desde el primer arranque.
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolJpaRepository rolJpaRepository;

    // CORRECCIÓN BUG 2: se agrega UsuarioJpaRepository para crear un usuario
    // de prueba con contraseña encriptada con BCrypt al iniciar la aplicación.
    @Autowired
    private UsuarioJpaRepository usuarioJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Inicializando datos base ===");

        RolJpaEntity adminRol;
        RolJpaEntity userRol;

        // Si la tabla de roles está vacía, crear ADMIN y USER; si no, recuperarlos
        if (rolJpaRepository.count() == 0) {
            System.out.println("Creando roles...");

            adminRol = new RolJpaEntity();
            adminRol.setNombreRol("ADMIN");
            adminRol = rolJpaRepository.save(adminRol);

            userRol = new RolJpaEntity();
            userRol.setNombreRol("USER");
            userRol = rolJpaRepository.save(userRol);

            System.out.println("Roles creados exitosamente");
        } else {
            System.out.println("Roles ya existen: " + rolJpaRepository.count());
            adminRol = rolJpaRepository.findByNombreRol("ADMIN").orElseThrow(
                    () -> new RuntimeException("Rol ADMIN no encontrado en la BD")
            );
            userRol = rolJpaRepository.findByNombreRol("USER").orElseThrow(
                    () -> new RuntimeException("Rol USER no encontrado en la BD")
            );
        }

        // Crear usuario admin de prueba con contraseña encriptada en BCrypt.
        // Sin esto, cualquier usuario insertado manualmente en la BD tendría
        // la contraseña en texto plano y BCrypt.checkpw() fallaría al hacer login.
        if (!usuarioJpaRepository.existsByCorreo("admin@sentimentapi.com")) {
            UsuarioJpaEntity admin = new UsuarioJpaEntity(
                    "Admin",
                    "Sistema",
                    BCrypt.hashpw("admin123", BCrypt.gensalt()),
                    "admin@sentimentapi.com",
                    List.of(adminRol)
            );
            usuarioJpaRepository.save(admin);
            System.out.println(">>> Usuario admin creado: admin@sentimentapi.com / admin123");
        }

        // Crear usuario regular de prueba
        if (!usuarioJpaRepository.existsByCorreo("user@sentimentapi.com")) {
            UsuarioJpaEntity user = new UsuarioJpaEntity(
                    "Usuario",
                    "Prueba",
                    BCrypt.hashpw("user123", BCrypt.gensalt()),
                    "user@sentimentapi.com",
                    List.of(userRol)
            );
            usuarioJpaRepository.save(user);
            System.out.println(">>> Usuario prueba creado: user@sentimentapi.com / user123");
        }

        System.out.println("=== Inicializacion completada ===");
    }
}