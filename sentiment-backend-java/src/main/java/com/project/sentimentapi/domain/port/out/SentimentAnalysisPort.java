package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.presentation.dto.response.SentimentsResponseDto;

import java.util.List;
import java.util.Optional;

// PORT OUT (puerto de salida — capa domain). Contrato que el dominio NECESITA para
// analizar sentimientos, sin saber quién lo cumple. Lo implementa SentimentApiAdapter
// (infrastructure) usando WebClient. Barrera DIP + base del OCP: para cambiar de
// proveedor de IA se crea otro adapter que implemente este port, sin tocar el use case.
public interface SentimentAnalysisPort {
    // Envía un lote de textos y devuelve la respuesta del modelo; Optional.empty()
    // o excepción si el servicio externo no responde.
    Optional<SentimentsResponseDto> analizarLote(List<String> textos);
}