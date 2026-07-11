package com.project.sentimentapi.infrastructure.persistence.repository;

import com.project.sentimentapi.infrastructure.persistence.entity.CategoriaJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.ProductoJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// REPOSITORIO SPRING DATA JPA (capa infrastructure). CRUD automático + consultas derivadas
// por nombre y consultas @Query (JPQL) para rankings de productos. Lo usa ProductoRepositoryAdapter.
@Repository
public interface ProductoJpaRepository extends JpaRepository<ProductoJpaEntity, Integer> {
    List<ProductoJpaEntity> findByUsuarioOrderByUltimaActualizacionDesc(UsuarioJpaEntity usuario);
    List<ProductoJpaEntity> findByUsuario(UsuarioJpaEntity usuario);
    List<ProductoJpaEntity> findByCategoriaOrderByNombreProductoAsc(CategoriaJpaEntity categoria);
    Optional<ProductoJpaEntity> findByNombreProductoAndUsuario(String nombreProducto, UsuarioJpaEntity usuario);
    Optional<ProductoJpaEntity> findByNombreProductoIgnoreCaseAndCategoriaAndUsuario(
            String nombreProducto, CategoriaJpaEntity categoria, UsuarioJpaEntity usuario);
    Optional<ProductoJpaEntity> findByNombreProductoIgnoreCase(String nombreProducto);

    @Query("SELECT p FROM ProductoJpaEntity p WHERE p.usuario = :usuario ORDER BY p.totalMenciones DESC")
    List<ProductoJpaEntity> findTopProductosByMenciones(@Param("usuario") UsuarioJpaEntity usuario);

    @Query("SELECT p FROM ProductoJpaEntity p WHERE p.usuario = :usuario AND p.totalMenciones > 0 " +
           "ORDER BY (CAST(p.positivos AS double) / p.totalMenciones) DESC")
    List<ProductoJpaEntity> findTopProductosByPositividad(@Param("usuario") UsuarioJpaEntity usuario);
}
