package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.dto.UserDtoRegistro;

public interface RegistrarUsuarioUseCase {
    void registrar(UserDtoRegistro request);
}