package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.RolJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolJpaRepository extends JpaRepository<RolJpaEntity, Integer> {
    Optional<RolJpaEntity> findByNombreRol(String nombreRol);
}
