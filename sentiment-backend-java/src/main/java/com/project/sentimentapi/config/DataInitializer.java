package com.project.sentimentapi.config;

import com.project.sentimentapi.entity.Rol;
import com.project.sentimentapi.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Inicializando datos base ===");
        if (rolRepository.count() == 0) {
            System.out.println("Creando roles...");
            Rol admin = new Rol();
            admin.setNombreRol("ADMIN");
            rolRepository.save(admin);
            Rol user = new Rol();
            user.setNombreRol("USER");
            rolRepository.save(user);
            System.out.println("Roles creados exitosamente");
        } else {
            System.out.println("Roles ya existen: " + rolRepository.count());
        }
        System.out.println("=== Inicializacion completada ===");
    }
}