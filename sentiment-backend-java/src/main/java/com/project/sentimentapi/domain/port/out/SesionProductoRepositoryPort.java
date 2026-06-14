package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.SesionProducto;

import java.util.List;

public interface SesionProductoRepositoryPort {
    SesionProducto guardar(SesionProducto sesionProducto);
    List<SesionProducto> guardarTodos(List<SesionProducto> items);
    List<SesionProducto> buscarPorSesion(Integer sesionId);
    List<SesionProducto> buscarProductosUltimaSesion(Integer usuarioId);
}