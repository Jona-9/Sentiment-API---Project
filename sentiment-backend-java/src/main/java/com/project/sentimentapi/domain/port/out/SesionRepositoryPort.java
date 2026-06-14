package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Sesion;

import java.util.List;
import java.util.Optional;

public interface SesionRepositoryPort {
    Sesion guardar(Sesion sesion);
    Optional<Sesion> buscarPorId(Integer id);
    List<Sesion> buscarPorUsuario(Integer usuarioId);
}