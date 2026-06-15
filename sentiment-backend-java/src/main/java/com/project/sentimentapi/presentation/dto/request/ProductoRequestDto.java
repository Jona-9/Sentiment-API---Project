package com.project.sentimentapi.presentation.dto.request;

import lombok.Data;

@Data
public class ProductoRequestDto {
    private String nombreProducto;
    private Integer categoriaId;
}
