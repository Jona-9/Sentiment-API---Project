package com.project.sentimentapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDtoLogin {
    private Integer id;
    private String nombre;
    private String apellido;
    private String correo;
}
