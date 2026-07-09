package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.SesionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SesionJpaRepository extends JpaRepository<SesionJpaEntity, Integer> {
    // Orden canónico del historial: más reciente primero (contrato del endpoint GET /sesiones).
    // Desempate por sesionId DESC para un orden total estable cuando dos sesiones comparten fecha.
    List<SesionJpaEntity> findByUsuarioUsuarioIDOrderByFechaDescSesionIdDesc(Integer usuarioId);
}
