package com.project.sentimentapi.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoMencionesDto {
    private String nombreProducto;
    private Integer totalMencionesEnSesion;
    private Integer positivosEnSesion;
    private Integer negativosEnSesion;
    private Integer neutralesEnSesion;
    private Double porcentajeMenciones;
}
