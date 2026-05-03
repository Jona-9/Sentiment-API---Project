package com.project.sentimentapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SesionDto {
    @JsonProperty("sessionId")
    private Integer sesionId;

    @JsonProperty("date")
    private String fecha;

    private Double avgScore;
    private Integer total;
    private Integer positivos;
    private Integer negativos;
    private Integer neutrales;
// ✅ AGREGAR ESTOS CAMPOS A SesionDto EXISTENTE

    private Integer productoId;
    private String nombreProducto;

    // ✅ Estadísticas del producto en ESTA sesión
    private ProductoMencionesDto productoMenciones;
    // ✅ NUEVO: Lista de comentarios analizados
    private List<ComentarioDto> comentarios;

    public SesionDto(Integer sesionId, String fecha, Double avgScore, Integer total,
                     Integer positivos, Integer negativos, Integer neutrales,
                     List<ComentarioDto> comentarios) {
        this.sesionId = sesionId;
        this.fecha = fecha;
        this.avgScore = avgScore;
        this.total = total;
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.comentarios = comentarios;
    }

    private List<ProductoMencionesDto> productosDetectados;

    public void setProductosDetectados(List<ProductoMencionesDto> productosDetectados) {
        this.productosDetectados = productosDetectados;
    }

    public List<ProductoMencionesDto> getProductosDetectados() {
        return productosDetectados;
    }
}