package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepositoryPort {
    List<Categoria> obtenerPorUsuario(Integer usuarioId);
    Optional<Categoria> buscarPorNombreYUsuario(String nombre, Integer usuarioId);
    Categoria guardar(Categoria categoria);
}