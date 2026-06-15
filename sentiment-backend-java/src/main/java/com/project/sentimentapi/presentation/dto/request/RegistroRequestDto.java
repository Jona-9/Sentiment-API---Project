package com.project.sentimentapi.presentation.dto.request;

import lombok.Data;

// Equivalente a dto/UserDtoRegistro.java con nombre más descriptivo
@Data
public class RegistroRequestDto {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
}
