package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.RolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// REPOSITORIO SPRING DATA JPA (capa infrastructure). CRUD de roles; findByNombreRol
// permite buscar "USER"/"ADMIN" al inicializar datos y al asignar rol en el registro.
@Repository
public interface RolJpaRepository extends JpaRepository<RolJpaEntity, Integer> {
    Optional<RolJpaEntity> findByNombreRol(String nombreRol);
}
