package com.project.sentimentapi.repository;

import com.project.sentimentapi.entity.Sesion;
import com.project.sentimentapi.entity.SesionProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
 public interface SesionProductoRepository extends JpaRepository<SesionProducto, Integer> {

    // Obtener todos los productos de una sesión específica
    List<SesionProducto> findBySesion(Sesion sesion);

    // Obtener los productos de la última sesión del usuario
    @Query("SELECT sp FROM SesionProducto sp WHERE sp.sesion.usuario.usuarioID = :usuarioId " +
            "AND sp.sesion.sesionId = (SELECT MAX(s.sesionId) FROM Sesion s WHERE s.usuario.usuarioID = :usuarioId)")
    List<SesionProducto> findProductosUltimaSesion(@Param("usuarioId") Integer usuarioId);
}
