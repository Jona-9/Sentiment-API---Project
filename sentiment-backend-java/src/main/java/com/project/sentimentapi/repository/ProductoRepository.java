package com.project.sentimentapi.repository;

import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.Producto;
import com.project.sentimentapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByUsuarioOrderByUltimaActualizacionDesc(User usuario);

    List<Producto> findByUsuario(User usuario);

    List<Producto> findByCategoriaOrderByNombreProductoAsc(Categoria categoria);

    Optional<Producto> findByNombreProductoAndUsuario(String nombreProducto, User usuario);

    Optional<Producto> findByNombreProductoIgnoreCaseAndCategoriaAndUsuario(String nombreProducto, Categoria categoria, User usuario);

    @Query("SELECT p FROM Producto p WHERE p.usuario = :usuario ORDER BY p.totalMenciones DESC")
    List<Producto> findTopProductosByMenciones(@Param("usuario") User usuario);

    @Query("SELECT p FROM Producto p WHERE p.usuario = :usuario AND p.totalMenciones > 0 ORDER BY (CAST(p.positivos AS double) / p.totalMenciones) DESC")
    List<Producto> findTopProductosByPositividad(@Param("usuario") User usuario);
}
