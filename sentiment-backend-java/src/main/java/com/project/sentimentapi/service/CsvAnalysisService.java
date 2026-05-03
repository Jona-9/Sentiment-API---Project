package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.CsvUploadRequestDto.CsvRowDto;
import com.project.sentimentapi.dto.CsvAnalysisResponseDto;
import java.util.List;

public interface CsvAnalysisService {
    
    CsvAnalysisResponseDto procesarYAnalizarCsv(List<CsvRowDto> rows, Integer usuarioId);
    
    List<CsvAnalysisResponseDto.ProductoAnalisisDto> obtenerComparativaProductos(Integer usuarioId);
    
    List<CsvAnalysisResponseDto.CategoriaAnalisisDto> obtenerComparativaCategorias(Integer usuarioId);
}
