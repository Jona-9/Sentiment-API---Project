package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.presentation.dto.response.*;
public class UsuarioMapper {

    private UsuarioMapper() {}

    public static UserDto toDto(Usuario usuario) {
        if (usuario == null) return null;
        UserDto dto = new UserDto();
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setCorreo(usuario.getEmail());
        return dto;
    }
}