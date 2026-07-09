package com.project.sentimentapi.presentation.dto.request;

import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import lombok.Data;

import java.util.List;

@Data
public class CsvBatchRequestDto {
    private List<CsvEntradaDto> entradas;
}
