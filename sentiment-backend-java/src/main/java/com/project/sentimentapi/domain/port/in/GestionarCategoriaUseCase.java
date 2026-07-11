package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.response.CategoriaDto;

import java.util.List;


// PORT IN (puerto de entrada — capa domain). Contrato de gestión de categorías
// (crear y consultar) para un usuario. Barrera DIP hacia CategoriaController.
public interface GestionarCategoriaUseCase {
    CategoriaDto crearCategoria(String nombreCategoria, String descripcion, Integer usuarioId);
    List<CategoriaDto> obtenerCategoriasPorUsuario(Integer usuarioId);
    CategoriaDto obtenerCategoriaPorId(Integer categoriaId, Integer usuarioId);
}