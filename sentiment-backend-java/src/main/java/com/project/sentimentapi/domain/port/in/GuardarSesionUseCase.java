package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.dto.SesionDto;

public interface GuardarSesionUseCase {
    SesionDto guardar(Sesion sesion);
}