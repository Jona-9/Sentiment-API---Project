package com.project.sentimentapi.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SesionPreviaInfoDto {
    private Integer sesionId;
    private String fecha;
    private Integer totalProductosAnalizados;
    private List<ProductoPrevioDto> productos;
}
