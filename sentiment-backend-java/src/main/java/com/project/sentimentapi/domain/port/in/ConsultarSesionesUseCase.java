package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.response.SesionDto;

import java.util.List;

// PORT IN (puerto de entrada — capa domain). Contrato de SOLO LECTURA del historial.
// ISP en acción: las operaciones de lectura (consultar) están separadas de la de
// escritura (GuardarSesionUseCase), para que un consumidor de solo lectura no
// dependa de métodos de escritura que nunca usará.
public interface ConsultarSesionesUseCase {
    List<SesionDto> obtenerPorUsuario(Integer usuarioId); // historial del usuario
    SesionDto obtenerPorId(Integer sesionId);             // detalle de una sesión
}