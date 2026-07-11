package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Producto;

import java.util.List;
import java.util.Optional;

// PORT OUT (puerto de salida — capa domain). Contrato de persistencia de Producto
// expresado en términos del DOMINIO (recibe/devuelve el modelo Producto, no la
// entidad JPA). Lo implementa ProductoRepositoryAdapter. Barrera DIP: el use case
// depende de esta interfaz; si se cambia de MySQL a otra BD, solo cambia el adapter.
public interface ProductoRepositoryPort {
    List<Producto> obtenerActivos();
    Optional<Producto> buscarPorId(Integer id);
    Optional<Producto> buscarPorNombre(String nombre);
    Producto guardar(Producto producto);
    List<Producto> guardarTodos(List<Producto> productos);
}