package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Producto;
import com.project.sentimentapi.domain.port.out.ProductoRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.ProductoJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.ProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Patrón ADAPTER: convierte entre ProductoRepositoryPort (dominio) y ProductoJpaRepository (JPA)
@Component
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private final ProductoJpaRepository jpaRepository;

    public ProductoRepositoryAdapter(ProductoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Producto> obtenerActivos() {
        // La entidad JPA no tiene campo "activo"; se retornan todos los productos
        return jpaRepository.findAll().stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(this::entityToDomain);
    }

    @Override
    public Optional<Producto> buscarPorNombre(String nombre) {
        return jpaRepository.findByNombreProductoIgnoreCase(nombre)
                .map(this::entityToDomain);
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoJpaEntity entity;
        if (producto.getId() != null) {
            // Fetch-then-update para preservar relaciones (categoria, usuario) ya existentes en BD
            entity = jpaRepository.findById(producto.getId())
                    .orElse(new ProductoJpaEntity());
        } else {
            entity = new ProductoJpaEntity();
        }
        entity.setNombreProducto(producto.getNombre());
        if (producto.getTotalMenciones() != null) entity.setTotalMenciones(producto.getTotalMenciones());
        if (producto.getPositivos() != null) entity.setPositivos(producto.getPositivos());
        if (producto.getNegativos() != null) entity.setNegativos(producto.getNegativos());
        if (producto.getNeutrales() != null) entity.setNeutrales(producto.getNeutrales());
        return entityToDomain(jpaRepository.save(entity));
    }

    @Override
    public List<Producto> guardarTodos(List<Producto> productos) {
        List<ProductoJpaEntity> entities = productos.stream()
                .map(p -> {
                    ProductoJpaEntity entity = (p.getId() != null)
                            ? jpaRepository.findById(p.getId()).orElse(new ProductoJpaEntity())
                            : new ProductoJpaEntity();
                    entity.setNombreProducto(p.getNombre());
                    if (p.getTotalMenciones() != null) entity.setTotalMenciones(p.getTotalMenciones());
                    if (p.getPositivos() != null) entity.setPositivos(p.getPositivos());
                    if (p.getNegativos() != null) entity.setNegativos(p.getNegativos());
                    if (p.getNeutrales() != null) entity.setNeutrales(p.getNeutrales());
                    return entity;
                })
                .collect(Collectors.toList());
        return jpaRepository.saveAll(entities).stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    private Producto entityToDomain(ProductoJpaEntity entity) {
        return new Producto(
                entity.getProductoId(),
                entity.getNombreProducto(),           // nombreProducto → nombre
                entity.getCategoria() != null ? entity.getCategoria().getCategoriaId() : null,
                entity.getTotalMenciones(),
                entity.getPositivos(),
                entity.getNegativos(),
                entity.getNeutrales()
        );
    }
}
