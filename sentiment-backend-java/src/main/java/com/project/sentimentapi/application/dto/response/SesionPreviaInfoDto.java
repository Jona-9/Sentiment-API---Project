package com.project.sentimentapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Resumen de la sesión anterior de un producto, para
// mostrar la comparativa "sesión previa vs. actual".
public class SesionPreviaInfoDto {
    private Integer sesionId;
    private String fecha;
    private Integer totalProductosAnalizados;
    private List<ProductoPrevioDto> productos;
}
