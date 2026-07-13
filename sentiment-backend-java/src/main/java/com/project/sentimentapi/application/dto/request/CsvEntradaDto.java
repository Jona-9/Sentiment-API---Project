package com.project.sentimentapi.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// DTO DE ENTRADA (capa presentation). Representa UNA fila del CSV que sube el dashboard
// (categoría, producto y el texto del comentario). Es la unidad que consume AnalizarCsvUseCase.
public class CsvEntradaDto {
    private String texto;
    private String producto;
    private String categoria;
}
