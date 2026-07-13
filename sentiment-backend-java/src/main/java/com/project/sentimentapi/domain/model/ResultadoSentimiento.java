package com.project.sentimentapi.domain.model;

// MODELO DE DOMINIO (capa domain). POJO puro que representa el resultado del análisis
// de sentimiento de UN texto, independiente del proveedor de IA que lo produjo.
// Es el tipo que devuelve el puerto de salida SentimentAnalysisPort, de modo que el
// dominio NO conoce el DTO de transporte (SentimentsResponseDto) de la API externa.
public class ResultadoSentimiento {

    private final String prevision;      // Positivo / Negativo / Neutro
    private final Double probabilidad;   // confianza del modelo [0..1]

    public ResultadoSentimiento(String prevision, Double probabilidad) {
        this.prevision = prevision;
        this.probabilidad = probabilidad;
    }

    public String getPrevision() { return prevision; }
    public Double getProbabilidad() { return probabilidad; }
}
