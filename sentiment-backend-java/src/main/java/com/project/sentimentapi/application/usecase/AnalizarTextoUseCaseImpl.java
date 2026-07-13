package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.application.port.in.AnalizarTextoUseCase;
import com.project.sentimentapi.domain.model.ResultadoSentimiento;
import com.project.sentimentapi.domain.port.out.SentimentAnalysisPort;
import com.project.sentimentapi.application.dto.response.ResponseDto;
import com.project.sentimentapi.application.dto.response.SentimentsResponseDto;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Use case dedicado al endpoint de diagnóstico /api/sentiment/analyze
@Service
public class AnalizarTextoUseCaseImpl implements AnalizarTextoUseCase {

    private final SentimentAnalysisPort sentimentPort;

    public AnalizarTextoUseCaseImpl(SentimentAnalysisPort sentimentPort) {
        this.sentimentPort = sentimentPort;
    }

    @Override
    public Optional<ResponseDto> analizarTexto(String texto) {
        return sentimentPort.analizarLote(List.of(texto))
                .map(resultados -> toResponseDto(resultados.get(0)));
    }

    // CORRECCIÓN BUG 1: antes se enviaba List.of(texto) — un solo elemento sin importar
    // cuántas líneas tuviera el texto. Ahora se separa por \n y se filtra vacíos.
    @Override
    public Optional<SentimentsResponseDto> analizarBatch(String texto) {
        List<String> textos = Arrays.stream(texto.split("\\n"))
                .map(String::trim)
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());

        if (textos.isEmpty()) return Optional.empty();

        return sentimentPort.analizarLote(textos).map(this::toSentimentsResponseDto);
    }

    // Mapea los modelos de dominio al DTO de salida que consume el frontend.
    private SentimentsResponseDto toSentimentsResponseDto(List<ResultadoSentimiento> resultados) {
        SentimentsResponseDto dto = new SentimentsResponseDto();
        dto.setResults(resultados.stream().map(this::toResponseDto).collect(Collectors.toList()));
        dto.setTotal(resultados.size());
        return dto;
    }

    private ResponseDto toResponseDto(ResultadoSentimiento r) {
        return new ResponseDto(r.getPrevision(), r.getProbabilidad());
    }
}