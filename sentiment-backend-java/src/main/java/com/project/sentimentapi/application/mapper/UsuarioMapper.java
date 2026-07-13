package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.application.dto.response.*;
// MAPPER (capa application). Traduce el modelo de dominio Usuario al DTO de salida
// UserDto. Aísla el dominio de la presentación (no se expone passwordHash al exterior).
// Métodos estáticos + constructor privado: es una utilidad sin estado, no se instancia.
public class UsuarioMapper {

    private UsuarioMapper() {} // constructor privado: clase de utilidad, no instanciable

    public static UserDto toDto(Usuario usuario) {
        if (usuario == null) return null;
        UserDto dto = new UserDto();
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getEmail());
        return dto;
    }
}