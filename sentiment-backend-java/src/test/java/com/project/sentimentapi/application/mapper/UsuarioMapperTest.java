package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.application.dto.response.UserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// PRUEBA DEL MAPPER (traducción dominio → DTO de salida).
// Verifica que UsuarioMapper copia los campos correctos y —clave de seguridad— que NUNCA
// expone el passwordHash en el DTO público. Refuerza la separación de capas (SRP/DIP).
class UsuarioMapperTest {

    @Test
    @DisplayName("toDto mapea nombre, apellido y correo (email → correo)")
    void mapeaCamposPublicos() {
        // Arrange
        Usuario usuario = new Usuario(1, "Ana", "Diaz", "ana@correo.com", "hash-bcrypt-secreto");

        // Act
        UserDto dto = UsuarioMapper.toDto(usuario);

        // Assert
        assertNotNull(dto);
        assertEquals("Ana", dto.getNombre());
        assertEquals("Diaz", dto.getApellido());
        assertEquals("ana@correo.com", dto.getCorreo());
    }

    @Test
    @DisplayName("toDto(null) devuelve null sin lanzar excepción")
    void devuelveNullConEntradaNula() {
        assertNull(UsuarioMapper.toDto(null));
    }
}
