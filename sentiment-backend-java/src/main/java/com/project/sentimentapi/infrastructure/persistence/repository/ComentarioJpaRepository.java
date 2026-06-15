package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.ComentarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioJpaRepository extends JpaRepository<ComentarioJpaEntity, Integer> {
    List<ComentarioJpaEntity> findBySesionSesionId(Integer sesionId);
}
