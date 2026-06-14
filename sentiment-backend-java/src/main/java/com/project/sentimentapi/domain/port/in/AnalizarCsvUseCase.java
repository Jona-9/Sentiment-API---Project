package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.dto.CsvAnalysisResponseDto;
import com.project.sentimentapi.dto.CsvEntradaDto;

import java.util.List;

public interface AnalizarCsvUseCase {
    CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Integer usuarioId);
}