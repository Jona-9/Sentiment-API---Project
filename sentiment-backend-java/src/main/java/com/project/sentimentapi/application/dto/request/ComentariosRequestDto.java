package com.project.sentimentapi.application.dto.request;

import lombok.Data;

import java.util.List;

@Data
// DTO DE ENTRADA (capa presentation). Transporta la lista de comentarios que el cliente
// envía para analizar. Los DTOs desacoplan la API pública de los modelos de dominio:
// cambiar el JSON externo no obliga a tocar el dominio.
public class ComentariosRequestDto {
    private List<String> comentarios;
}
