package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvAnalysisResponseDto {
    private Integer sesionId;
    private String fecha;
    private Integer totalComentarios;
    private Integer totalPositivos;
    private Integer totalNegativos;
    private Integer totalNeutrales;
    private Double avgScore;
    private List<CategoriaAnalisisDto> categorias;
    private List<ProductoAnalisisDto> productos;
    private List<ComentarioDto> comentarios;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaAnalisisDto {
        private Integer categoriaId;
        private String nombreCategoria;
        private Integer totalComentarios;
        private Integer positivos;
        private Integer negativos;
        private Integer neutrales;
        private Double porcentajePositivo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoAnalisisDto {
        private Integer productoId;
        private String nombreProducto;
        private String categoria;
        private Integer totalComentarios;
        private Integer positivos;
        private Integer negativos;
        private Integer neutrales;
        private Double porcentajePositivo;
    }
}
