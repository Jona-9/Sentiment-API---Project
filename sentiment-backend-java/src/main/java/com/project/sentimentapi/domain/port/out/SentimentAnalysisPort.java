package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.ResultadoSentimiento;

import java.util.List;
import java.util.Optional;

// PORT OUT (puerto de salida — capa domain). Contrato que el dominio NECESITA para
// analizar sentimientos, sin saber quién lo cumple. Lo implementa SentimentApiAdapter
// (infrastructure) usando WebClient. Barrera DIP + base del OCP: para cambiar de
// proveedor de IA se crea otro adapter que implemente este port, sin tocar el use case.
// Devuelve modelos de dominio (ResultadoSentimiento), no el DTO de transporte de la API,
// para no acoplar el dominio a la forma del JSON externo.
public interface SentimentAnalysisPort {
    // Envía un lote de textos y devuelve un resultado por texto (mismo orden que la
    // entrada); Optional.empty() o excepción si el servicio externo no responde.
    Optional<List<ResultadoSentimiento>> analizarLote(List<String> textos);
}
