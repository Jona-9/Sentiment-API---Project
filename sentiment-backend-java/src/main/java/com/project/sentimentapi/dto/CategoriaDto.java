package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriaDto {
    private Integer categoriaId;
    private String nombreCategoria;
    private String descripcion;
    private Integer totalProductos; // Cantidad de productos en esta categoría
}
