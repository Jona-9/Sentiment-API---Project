package com.project.sentimentapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class CsvBatchRequestDto {
    private List<CsvEntradaDto> entradas;
}
