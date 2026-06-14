package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.dto.SesionDto;

import java.util.List;

public interface ConsultarSesionesUseCase {
    List<SesionDto> obtenerPorUsuario(Integer usuarioId);
    SesionDto obtenerPorId(Integer sesionId);
}