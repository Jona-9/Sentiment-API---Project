package com.project.sentimentapi.application.dto.request;

import com.project.sentimentapi.application.dto.request.CsvEntradaDto;
import lombok.Data;

import java.util.List;

@Data
// DTO DE ENTRADA (capa presentation). Envuelve un lote de textos para análisis masivo.
public class CsvBatchRequestDto {
    private List<CsvEntradaDto> entradas;
}
