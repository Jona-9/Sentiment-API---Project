package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// REPOSITORIO SPRING DATA JPA (capa infrastructure). Al extender JpaRepository, Spring
// genera automáticamente el CRUD. Los métodos "findBy..." son consultas derivadas: Spring
// crea el SQL a partir del nombre del método. Lo consume UsuarioRepositoryAdapter, no el dominio.
@Repository
public interface UsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Integer> {
    Optional<UsuarioJpaEntity> findByCorreo(String correo);
    Optional<UsuarioJpaEntity> findByResetToken(String resetToken);
    boolean existsByCorreo(String correo);
}
