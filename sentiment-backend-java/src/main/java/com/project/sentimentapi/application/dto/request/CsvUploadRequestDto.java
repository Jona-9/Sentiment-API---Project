package com.project.sentimentapi.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
// DTO DE ENTRADA (capa presentation). Representa la carga de un CSV completo (conjunto
// de filas) tal como llega desde el cliente.
public class CsvUploadRequestDto {
    private List<CsvRowDto> rows;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CsvRowDto {
        private String categoria;
        private String producto;
        private String comentario;
    }
}
