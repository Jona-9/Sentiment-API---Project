package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.presentation.dto.response.SesionDto;

public interface GuardarSesionUseCase {
    SesionDto guardar(Sesion sesion);
}