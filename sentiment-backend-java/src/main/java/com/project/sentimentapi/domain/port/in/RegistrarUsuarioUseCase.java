package com.project.sentimentapi.domain.port.in;

import com.project.sentimentapi.presentation.dto.request.RegistroRequestDto;

// PORT IN (puerto de entrada — capa domain). Contrato del caso de uso de registro.
// Barrera DIP: UsuarioController depende de esta abstracción, no de la clase concreta.
public interface RegistrarUsuarioUseCase {
    void registrar(RegistroRequestDto request);
}