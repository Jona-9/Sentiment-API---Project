package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.port.in.AnalizarTextoUseCase;
import com.project.sentimentapi.domain.port.out.SentimentAnalysisPort;
import com.project.sentimentapi.presentation.dto.response.ResponseDto;
import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;
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
                .map(r -> r.getResults().get(0));
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

        return sentimentPort.analizarLote(textos);
    }
}