package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.RegistroRequestDto;

public interface RegistrarUsuarioUseCase {
    void registrar(RegistroRequestDto request);
}