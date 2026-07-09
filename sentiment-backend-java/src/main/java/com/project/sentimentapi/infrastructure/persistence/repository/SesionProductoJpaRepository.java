package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.SesionJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.SesionProductoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SesionProductoJpaRepository extends JpaRepository<SesionProductoJpaEntity, Integer> {
    List<SesionProductoJpaEntity> findBySesion(SesionJpaEntity sesion);

    @Query("SELECT sp FROM SesionProductoJpaEntity sp " +
           "WHERE sp.sesion.usuario.usuarioID = :usuarioId " +
           "AND sp.sesion.sesionId = (SELECT MAX(s.sesionId) FROM SesionJpaEntity s " +
           "WHERE s.usuario.usuarioID = :usuarioId)")
    List<SesionProductoJpaEntity> findProductosUltimaSesion(@Param("usuarioId") Integer usuarioId);
}
