package com.project.sentimentapi.application.port.in;

import com.project.sentimentapi.application.dto.response.CsvAnalysisResponseDto;
import com.project.sentimentapi.application.dto.request.CsvEntradaDto;

import java.util.List;

// PORT IN (puerto de entrada — capa domain). Contrato de caso de uso que la capa
// de presentación (controller) invoca. Es la barrera DIP: el controller depende
// de esta interfaz, no de la implementación AnalizarCsvUseCaseImpl.
// ISP: expone un único método cohesionado (analizar), sin obligar a nadie a
// depender de operaciones que no usa.
public interface AnalizarCsvUseCase {
    // Recibe las filas del CSV y el id del usuario autenticado; devuelve el DTO
    // con estadísticas globales, por categoría, por producto y el detalle.
    CsvAnalysisResponseDto analizar(List<CsvEntradaDto> filas, Integer usuarioId);
}