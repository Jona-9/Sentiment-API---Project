package com.project.sentimentapi.presentation.dto.response;

import lombok.Data;

@Data
public class UserDto {
    private String nombre;
    private String apellido;
    private String correo;
}
