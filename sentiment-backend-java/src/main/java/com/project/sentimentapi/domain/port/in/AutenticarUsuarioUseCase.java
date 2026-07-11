package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.LoginRequestDto;
import com.project.sentimentapi.presentation.dto.response.LoginResponseDto;

import java.util.Optional;

// PORT IN (puerto de entrada — capa domain). Contrato de login: valida credenciales
// y devuelve el DTO con el JWT. Optional.empty() si las credenciales no son válidas.
public interface AutenticarUsuarioUseCase {
    Optional<LoginResponseDto> autenticar(LoginRequestDto request);
}