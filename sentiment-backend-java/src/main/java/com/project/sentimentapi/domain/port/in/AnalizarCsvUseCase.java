package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.response.CsvAnalysisResponseDto;
import com.project.sentimentapi.presentation.dto.request.CsvEntradaDto;

import java.util.List;

public interface AnalizarCsvUseCase {
    CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Integer usuarioId);
}