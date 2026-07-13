package com.project.sentimentapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Producto con su número de menciones, para rankings.
public class ProductoMencionesDto {
    private String nombreProducto;
    private Integer totalMencionesEnSesion;
    private Integer positivosEnSesion;
    private Integer negativosEnSesion;
    private Integer neutralesEnSesion;
    private Double porcentajeMenciones;
}
