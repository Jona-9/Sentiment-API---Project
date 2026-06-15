package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.SesionJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SesionJpaRepository extends JpaRepository<SesionJpaEntity, Integer> {
    List<SesionJpaEntity> findByUsuarioOrderBySesionIdDesc(UsuarioJpaEntity usuario);
    List<SesionJpaEntity> findByUsuarioUsuarioID(Integer usuarioId);
}
