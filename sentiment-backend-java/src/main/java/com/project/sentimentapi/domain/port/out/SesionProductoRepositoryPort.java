package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.SesionProducto;

import java.util.List;

// PORT OUT (puerto de salida — capa domain). Contrato de persistencia de la relación
// Sesion↔Producto (desglose por producto dentro de una sesión). Lo implementa
// SesionProductoRepositoryAdapter. Barrera DIP.
public interface SesionProductoRepositoryPort {
    SesionProducto guardar(SesionProducto sesionProducto);
    List<SesionProducto> guardarTodos(List<SesionProducto> items);
    List<SesionProducto> buscarPorSesion(Integer sesionId);
    List<SesionProducto> buscarProductosUltimaSesion(Integer usuarioId);
}