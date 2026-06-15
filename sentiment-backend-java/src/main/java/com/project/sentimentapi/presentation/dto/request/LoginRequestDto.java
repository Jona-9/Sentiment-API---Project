package com.project.sentimentapi.presentation.dto.request;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String correo;
    private String contrasena;
}
