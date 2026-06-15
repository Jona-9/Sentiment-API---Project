package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Categoria;
import com.project.sentimentapi.domain.port.out.CategoriaRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.CategoriaJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.CategoriaJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Patrón ADAPTER: convierte entre CategoriaRepositoryPort (dominio) y CategoriaJpaRepository (JPA)
@Component
public class CategoriaRepositoryAdapter implements CategoriaRepositoryPort {

    private final CategoriaJpaRepository categoriaJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public CategoriaRepositoryAdapter(CategoriaJpaRepository categoriaJpaRepository,
                                       UsuarioJpaRepository usuarioJpaRepository) {
        this.categoriaJpaRepository = categoriaJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public List<Categoria> obtenerPorUsuario(Integer usuarioId) {
        return usuarioJpaRepository.findById(usuarioId)
                .map(usuario -> categoriaJpaRepository.findByUsuario(usuario)
                        .stream()
                        .map(this::entityToDomain)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public Optional<Categoria> buscarPorNombreYUsuario(String nombre, Integer usuarioId) {
        return usuarioJpaRepository.findById(usuarioId)
                .flatMap(usuario -> categoriaJpaRepository
                        .findByNombreCategoriaIgnoreCaseAndUsuario(nombre, usuario)
                        .map(this::entityToDomain));
    }

    @Override
    public Categoria guardar(Categoria categoria) {
        UsuarioJpaEntity usuario = usuarioJpaRepository.findById(categoria.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con id: " + categoria.getUsuarioId()));

        CategoriaJpaEntity entity;
        if (categoria.getId() != null) {
            entity = categoriaJpaRepository.findById(categoria.getId())
                    .orElse(new CategoriaJpaEntity());
        } else {
            entity = new CategoriaJpaEntity();
        }
        entity.setNombreCategoria(categoria.getNombre());
        entity.setDescripcion(categoria.getDescripcion());
        entity.setUsuario(usuario);

        return entityToDomain(categoriaJpaRepository.save(entity));
    }

    private Categoria entityToDomain(CategoriaJpaEntity entity) {
        return new Categoria(
                entity.getCategoriaId(),
                entity.getNombreCategoria(),
                entity.getDescripcion(),
                entity.getUsuario() != null ? entity.getUsuario().getUsuarioID() : null
        );
    }
}
