package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvEntradaDto {
    private String texto;
    private String producto;
    private String categoria;
}
