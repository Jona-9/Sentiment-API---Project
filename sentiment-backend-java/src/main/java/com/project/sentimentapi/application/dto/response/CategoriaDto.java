package com.project.sentimentapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Categoría lista para la UI (id, nombre, descripción,
// total de productos). Lo produce CategoriaMapper.
public class CategoriaDto {
    private Integer categoriaId;
    private String nombreCategoria;
    private String descripcion;
    private Integer totalProductos;
}
