package com.project.sentimentapi.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
// DTO DE SALIDA (capa presentation). Resultado individual del modelo de ML para UN texto:
// la previsión (Positivo/Negativo/Neutro) y la probabilidad/confianza asociada.
public class ResponseDto {
    private String prevision;
    private Double probabilidad;
}
