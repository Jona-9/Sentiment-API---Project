package com.project.sentimentapi.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Producto con sus contadores y porcentajes de
// sentimiento ya calculados, listo para que el dashboard lo grafique.
public class ProductoDto {
    private Integer productoId;
    private String nombreProducto;
    private Integer categoriaId;
    private String nombreCategoria;
    private Integer totalMenciones;
    private Integer positivos;
    private Integer negativos;
    private Integer neutrales;

    @JsonProperty("porcentajePositivos")
    private Double porcentajePositivos;

    @JsonProperty("porcentajeNegativos")
    private Double porcentajeNegativos;

    @JsonProperty("porcentajeNeutrales")
    private Double porcentajeNeutrales;

    private String fechaCreacion;
    private String ultimaActualizacion;
}
