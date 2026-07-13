package com.project.sentimentapi.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Sesión de análisis para el historial del dashboard:
// estadísticas globales y, opcionalmente, el detalle de comentarios. Lo produce SesionMapper.
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

    private Integer productoId;
    private String nombreProducto;

    private ProductoMencionesDto productoMenciones;
    private List<ComentarioDto> comentarios;
    private List<ProductoMencionesDto> productosDetectados;

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
}
