package com.project.sentimentapi.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Comentario ya analizado para mostrar en la UI:
// texto, sentimiento, probabilidad y el producto asociado.
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
