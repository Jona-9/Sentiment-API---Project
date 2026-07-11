package com.project.sentimentapi.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
// DTO DE SALIDA (capa presentation). Respuesta del login: datos públicos del usuario + el
// token JWT que el cliente guardará y enviará en cada petición protegida.
public class LoginResponseDto {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
    private String token;
}
