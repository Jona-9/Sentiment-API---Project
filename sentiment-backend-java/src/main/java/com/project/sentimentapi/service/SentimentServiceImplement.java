package com.project.sentimentapi.service;

import com.project.sentimentapi.configuration.ConectarApi;
import com.project.sentimentapi.dto.ResponseDto;
import com.project.sentimentapi.dto.SentimentsResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class SentimentServiceImplement implements SentimentService {
    @Autowired
    ConectarApi conectarApi;

    public Optional<ResponseDto> consultarSentimiento(String texto) {
        try {
            Map<String, String> request = new HashMap<>();
            request.put("text", texto);
            ResponseDto responseDto = conectarApi.client().post().uri("/predict").
                    bodyValue(request).
                    retrieve().bodyToMono(ResponseDto.class).block();
            return Optional.of(responseDto);
        } catch (WebClientRequestException e) {
            return Optional.empty();
        }
    }

    public Optional<SentimentsResponseDto> consultarSentimientos(String texto) {
        try {
            String[] textoConcatenado = texto.split("\n");
            List<String> textoGuardado = new ArrayList<>();
            for (String mostrar : textoConcatenado) {
                textoGuardado.add(mostrar);
            }
            Map<String, List<String>> request = new HashMap<>();
            request.put("texts", textoGuardado);
            ObjectMapper objectMapper = new ObjectMapper();
            String string = conectarApi.client().post().uri("/predict/batch").
                    bodyValue(request).
                    retrieve().bodyToMono(String.class).block();
            SentimentsResponseDto responseDto = objectMapper.readValue(string, SentimentsResponseDto.class);

            return Optional.of(responseDto);
        } catch (WebClientRequestException e) {
            return Optional.empty();
        }
    }
}
