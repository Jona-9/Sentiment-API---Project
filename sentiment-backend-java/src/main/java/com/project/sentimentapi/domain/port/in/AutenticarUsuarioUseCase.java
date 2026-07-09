package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.LoginRequestDto;
import com.project.sentimentapi.presentation.dto.response.LoginResponseDto;

import java.util.Optional;

public interface AutenticarUsuarioUseCase {
    Optional<LoginResponseDto> autenticar(LoginRequestDto request);
}