package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.application.mapper.SesionMapper;
import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.domain.port.in.GuardarSesionUseCase;
import com.project.sentimentapi.domain.port.out.SesionRepositoryPort;
import com.project.sentimentapi.presentation.dto.response.SesionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Principio ISP: interfaz separada de ConsultarSesionesUseCase.
// CsvAnalysisController solo inyecta esto; SesionController solo inyecta ConsultarSesiones.
@Service
public class GuardarSesionUseCaseImpl implements GuardarSesionUseCase {

    private final SesionRepositoryPort sesionPort;

    public GuardarSesionUseCaseImpl(SesionRepositoryPort sesionPort) {
        this.sesionPort = sesionPort;
    }

    @Override
    @Transactional
    public SesionDto guardar(Sesion sesion) {
        Sesion guardada = sesionPort.guardar(sesion);
        return SesionMapper.toDto(guardada);
    }
}