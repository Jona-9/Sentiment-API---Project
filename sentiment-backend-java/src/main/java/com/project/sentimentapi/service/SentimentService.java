package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SentimentsResponseDto;

import java.util.Optional;

public interface SentimentService  {
      Optional<ResponseDto> consultarSentimiento(String texto);
      Optional<SentimentsResponseDto> consultarSentimientos(String texto);
}
