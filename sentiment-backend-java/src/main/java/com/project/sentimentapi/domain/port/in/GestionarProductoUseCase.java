package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.ProductoRequestDto;
import com.project.sentimentapi.presentation.dto.response.ProductoDto;

import java.util.List;


// PORT IN (puerto de entrada — capa domain). Contrato de gestión de productos:
// crear, consultar (por usuario/categoría/id) y actualizar contadores de sentimiento.
public interface GestionarProductoUseCase {
    ProductoDto crearProducto(ProductoRequestDto request, Integer usuarioId);
    List<ProductoDto> obtenerProductosPorUsuario(Integer usuarioId);
    List<ProductoDto> obtenerProductosPorCategoria(Integer categoriaId, Integer usuarioId);
    ProductoDto obtenerProductoPorId(Integer productoId, Integer usuarioId);
    void actualizarContadoresProducto(Integer productoId, int positivos, int negativos, int neutrales);
}