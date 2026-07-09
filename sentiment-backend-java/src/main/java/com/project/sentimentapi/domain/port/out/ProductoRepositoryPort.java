package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepositoryPort {
    List<Producto> obtenerActivos();
    Optional<Producto> buscarPorId(Integer id);
    Optional<Producto> buscarPorNombre(String nombre);
    Producto guardar(Producto producto);
    List<Producto> guardarTodos(List<Producto> productos);
}