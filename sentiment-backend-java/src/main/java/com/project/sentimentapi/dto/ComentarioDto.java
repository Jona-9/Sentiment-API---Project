package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComentarioDto {
    private String texto;
    private String sentimiento;
    private Double probabilidad;
    private String productoAsociado;

    public ComentarioDto(String texto, String sentimiento, Double probabilidad) {
        this.texto = texto;
        this.sentimiento = sentimiento;
        this.probabilidad = probabilidad;
        this.productoAsociado = null;
    }
}