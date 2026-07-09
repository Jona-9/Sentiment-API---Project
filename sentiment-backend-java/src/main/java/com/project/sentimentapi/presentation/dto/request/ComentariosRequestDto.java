package com.project.sentimentapi.presentation.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ComentariosRequestDto {
    private List<String> comentarios;
}
