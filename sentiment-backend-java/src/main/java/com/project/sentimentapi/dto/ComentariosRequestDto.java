package com.project.sentimentapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class ComentariosRequestDto {
    private List<String> comentarios;
}