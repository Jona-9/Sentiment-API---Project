package com.project.sentimentapi.presentation.dto.request;

import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;
import lombok.Data;

import java.util.List;

@Data
// DTO DE ENTRADA (capa presentation). Envuelve un lote de textos para análisis masivo.
public class CsvBatchRequestDto {
    private List<CsvEntradaDto> entradas;
}
