package com.project.sentimentapi.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String token;
}
