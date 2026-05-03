package com.project.sentimentapi.dto;

import lombok.Data;
@Data
public class UserDtoRegistro {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
}