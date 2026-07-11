package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Sesion;

import java.util.List;
import java.util.Optional;

// PORT OUT (puerto de salida — capa domain). Contrato de persistencia de Sesion.
// Lo implementa SesionRepositoryAdapter. Barrera DIP.
public interface SesionRepositoryPort {
    Sesion guardar(Sesion sesion);
    Optional<Sesion> buscarPorId(Integer id);
    List<Sesion> buscarPorUsuario(Integer usuarioId);
}