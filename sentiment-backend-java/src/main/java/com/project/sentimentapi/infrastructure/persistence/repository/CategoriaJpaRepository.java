package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.CategoriaJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaJpaRepository extends JpaRepository<CategoriaJpaEntity, Integer> {
    List<CategoriaJpaEntity> findByUsuarioOrderByNombreCategoriaAsc(UsuarioJpaEntity usuario);
    List<CategoriaJpaEntity> findByUsuario(UsuarioJpaEntity usuario);
    Optional<CategoriaJpaEntity> findByNombreCategoriaAndUsuario(String nombreCategoria, UsuarioJpaEntity usuario);
    Optional<CategoriaJpaEntity> findByNombreCategoriaIgnoreCaseAndUsuario(String nombreCategoria, UsuarioJpaEntity usuario);
    long countByUsuario(UsuarioJpaEntity usuario);
}
