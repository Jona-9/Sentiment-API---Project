package com.project.sentimentapi.application.dto.response;

import lombok.Data;

@Data
// DTO DE SALIDA (capa presentation). Datos públicos del usuario (nombre, apellido, correo).
// Nunca incluye el passwordHash: por eso se usa un DTO en lugar de exponer el modelo Usuario.
public class UserDto {
    private String nombre;
    private String apellido;
    private String correo;
}
