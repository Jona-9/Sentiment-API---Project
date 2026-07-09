package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.response.ResponseDto;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;

import java.util.Optional;

public interface AnalizarTextoUseCase {
    Optional<ResponseDto> analizarTexto(String texto);
    Optional<SentimentsResponseDto> analizarBatch(String texto);
}