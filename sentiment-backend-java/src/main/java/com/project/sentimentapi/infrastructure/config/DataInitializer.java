package com.project.sentimentapi.infrastructure.config;

import com.project.sentimentapi.infrastructure.persistence.entity.RolJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.RolJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolJpaRepository rolJpaRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Inicializando datos base ===");
        if (rolJpaRepository.count() == 0) {
            System.out.println("Creando roles...");
            RolJpaEntity admin = new RolJpaEntity();
            admin.setNombreRol("ADMIN");
            rolJpaRepository.save(admin);
            RolJpaEntity user = new RolJpaEntity();
            user.setNombreRol("USER");
            rolJpaRepository.save(user);
            System.out.println("Roles creados exitosamente");
        } else {
            System.out.println("Roles ya existen: " + rolJpaRepository.count());
        }
        System.out.println("=== Inicializacion completada ===");
    }
}
