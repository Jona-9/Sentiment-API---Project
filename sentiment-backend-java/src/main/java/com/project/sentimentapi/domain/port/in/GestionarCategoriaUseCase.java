package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.dto.CategoriaDto;

import java.util.List;

public interface GestionarCategoriaUseCase {
    CategoriaDto crearCategoria(String nombreCategoria, String descripcion, Integer usuarioId);
    List<CategoriaDto> obtenerCategoriasPorUsuario(Integer usuarioId);
    CategoriaDto obtenerCategoriaPorId(Integer categoriaId, Integer usuarioId);
}