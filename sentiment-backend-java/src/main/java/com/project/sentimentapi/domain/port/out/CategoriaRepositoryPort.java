package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Categoria;

import java.util.List;
import java.util.Optional;

// PORT OUT (puerto de salida — capa domain). Contrato de persistencia de Categoria.
// Lo implementa CategoriaRepositoryAdapter. Barrera DIP.
public interface CategoriaRepositoryPort {
    List<Categoria> obtenerPorUsuario(Integer usuarioId);
    Optional<Categoria> buscarPorNombreYUsuario(String nombre, Integer usuarioId);
    Categoria guardar(Categoria categoria);
}