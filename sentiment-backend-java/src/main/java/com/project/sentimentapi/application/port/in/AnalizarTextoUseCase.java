package com.project.sentimentapi.application.port.in;

import com.project.sentimentapi.application.dto.response.ResponseDto;
import com.project.sentimentapi.application.dto.response.SentimentsResponseDto;

import java.util.Optional;

// PORT IN (puerto de entrada — capa domain). Contrato para analizar texto suelto
// (un comentario individual o un lote textual), separado del flujo de CSV. Barrera DIP.
public interface AnalizarTextoUseCase {
    Optional<ResponseDto> analizarTexto(String texto);
    Optional<SentimentsResponseDto> analizarBatch(String texto);
}