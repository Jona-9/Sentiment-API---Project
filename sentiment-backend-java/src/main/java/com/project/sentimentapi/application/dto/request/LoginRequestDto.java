package com.project.sentimentapi.application.dto.request;

import lombok.Data;

@Data
// DTO DE ENTRADA (capa presentation). Credenciales de login (correo y contraseña).
public class LoginRequestDto {
    private String correo;
    private String contrasena;
}
