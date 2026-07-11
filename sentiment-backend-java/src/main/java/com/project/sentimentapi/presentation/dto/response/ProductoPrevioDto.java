package com.project.sentimentapi.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Estado "previo" de un producto, usado para comparar
// su evolución entre sesiones (antes/después) en el dashboard.
public class ProductoPrevioDto {
    private Integer productoId;
    private String nombreProducto;
    private String nombreCategoria;
    private Integer mencionesEnUltimaSesion;
    private Integer positivosEnUltimaSesion;
    private Integer negativosEnUltimaSesion;
}
