package com.project.sentimentapi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class ProductoMencionesDto {
        private String nombreProducto;
        private Integer totalMencionesEnSesion; // Menciones solo en esta sesión
        private Integer positivosEnSesion;
        private Integer negativosEnSesion;
        private Integer neutralesEnSesion;
        private Double porcentajeMenciones; // % de comentarios que mencionan el producto
    }
