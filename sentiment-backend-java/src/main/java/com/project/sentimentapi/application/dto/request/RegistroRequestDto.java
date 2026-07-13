package com.project.sentimentapi.application.dto.request;

import lombok.Data;

// Equivalente a dto/UserDtoRegistro.java con nombre más descriptivo
@Data
// DTO DE ENTRADA (capa presentation). Datos que envía el formulario de registro
// (nombre, apellido, correo, contraseña). El use case los transforma en el modelo Usuario.
public class RegistroRequestDto {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
}
