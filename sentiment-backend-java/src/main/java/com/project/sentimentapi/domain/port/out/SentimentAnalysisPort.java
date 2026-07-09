package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;

import java.util.List;
import java.util.Optional;

public interface SentimentAnalysisPort {
    Optional<SentimentsResponseDto> analizarLote(List<String> textos);
}