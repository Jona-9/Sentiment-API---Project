package com.project.sentimentapi.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoPrevioDto {
    private Integer productoId;
    private String nombreProducto;
    private String nombreCategoria;
    private Integer mencionesEnUltimaSesion;
    private Integer positivosEnUltimaSesion;
    private Integer negativosEnUltimaSesion;
}
