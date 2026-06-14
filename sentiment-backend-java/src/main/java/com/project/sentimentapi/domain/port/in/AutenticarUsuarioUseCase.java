package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.dto.LoginResponseDto;
import com.project.sentimentapi.dto.UserDtoRegistro;

import java.util.Optional;

public interface AutenticarUsuarioUseCase {
    Optional<LoginResponseDto> autenticar(UserDtoRegistro request);
}