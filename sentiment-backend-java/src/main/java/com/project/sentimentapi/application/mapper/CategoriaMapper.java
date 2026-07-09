package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Categoria;
import com.project.sentimentapi.presentation.dto.response.*;
public class CategoriaMapper {

    private CategoriaMapper() {}

    public static CategoriaDto toDto(Categoria categoria) {
        if (categoria == null) return null;

        CategoriaDto dto = new CategoriaDto();
        dto.setCategoriaId(categoria.getId());
        dto.setNombreCategoria(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        dto.setTotalProductos(0);

        return dto;
    }
}