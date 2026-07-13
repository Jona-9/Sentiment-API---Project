package com.project.sentimentapi.application.dto.request;

import lombok.Data;

@Data
// DTO DE ENTRADA (capa presentation). Datos para crear un producto (nombre y categoría).
public class ProductoRequestDto {
    private String nombreProducto;
    private Integer categoriaId;
}
