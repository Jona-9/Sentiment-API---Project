package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.presentation.dto.response.SesionDto;

// PORT IN (puerto de entrada — capa domain). Contrato de SOLO ESCRITURA de sesiones.
// ISP: separado de ConsultarSesionesUseCase (lectura) para no mezclar responsabilidades.
public interface GuardarSesionUseCase {
    SesionDto guardar(Sesion sesion);
}