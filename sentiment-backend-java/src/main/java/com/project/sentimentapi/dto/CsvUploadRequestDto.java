package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
