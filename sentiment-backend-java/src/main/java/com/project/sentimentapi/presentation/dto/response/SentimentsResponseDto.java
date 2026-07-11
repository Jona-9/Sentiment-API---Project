package com.project.sentimentapi.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
// DTO DE SALIDA (capa presentation). Mapea la respuesta JSON de la API Python de ML:
// una lista de resultados (uno por texto enviado). Lo llena SentimentApiAdapter.
public class SentimentsResponseDto {
    private List<ResponseDto> results;
    private Integer total;
}
