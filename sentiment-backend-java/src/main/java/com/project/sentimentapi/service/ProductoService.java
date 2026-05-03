package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ProductoDto;
import com.project.sentimentapi.dto.ProductoRequestDto;
import java.util.List;

    public interface ProductoService {
        ProductoDto crearProducto(ProductoRequestDto request, Integer usuarioId);
        List<ProductoDto> obtenerProductosPorUsuario(Integer usuarioId);
        List<ProductoDto> obtenerProductosPorCategoria(Integer categoriaId, Integer usuarioId);
        ProductoDto obtenerProductoPorId(Integer productoId, Integer usuarioId);
        void actualizarContadoresProducto(Integer productoId, int positivos, int negativos, int neutrales);
    }
