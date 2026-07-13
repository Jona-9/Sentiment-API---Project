package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Categoria;
import com.project.sentimentapi.application.dto.response.*;
// MAPPER (capa application). Traduce el modelo de dominio Categoria al DTO CategoriaDto.
// Utilidad estática sin estado (constructor privado).
public class CategoriaMapper {

    private CategoriaMapper() {} // constructor privado: clase de utilidad, no instanciable

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