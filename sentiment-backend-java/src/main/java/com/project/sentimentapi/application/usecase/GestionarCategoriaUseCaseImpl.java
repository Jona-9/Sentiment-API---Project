package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.model.Categoria;
import com.project.sentimentapi.application.port.in.GestionarCategoriaUseCase;
import com.project.sentimentapi.domain.port.out.CategoriaRepositoryPort;
import com.project.sentimentapi.application.dto.response.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Principio SRP: solo gestiona categorías.
// Principio DIP: depende de CategoriaRepositoryPort (interfaz), no de CategoriaRepository (JPA).
@Service
public class GestionarCategoriaUseCaseImpl implements GestionarCategoriaUseCase {

    private final CategoriaRepositoryPort categoriaPort;

    public GestionarCategoriaUseCaseImpl(CategoriaRepositoryPort categoriaPort) {
        this.categoriaPort = categoriaPort;
    }

    @Override
    @Transactional
    public CategoriaDto crearCategoria(String nombreCategoria, String descripcion, Integer usuarioId) {
        // Validar duplicado
        categoriaPort.buscarPorNombreYUsuario(nombreCategoria, usuarioId)
                .ifPresent(c -> {
                    throw new RuntimeException("Ya existe una categoría con este nombre");
                });

        Categoria nueva = new Categoria(nombreCategoria, descripcion, usuarioId);
        Categoria guardada = categoriaPort.guardar(nueva);
        return toDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> obtenerCategoriasPorUsuario(Integer usuarioId) {
        return categoriaPort.obtenerPorUsuario(usuarioId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaDto obtenerCategoriaPorId(Integer categoriaId, Integer usuarioId) {
        return categoriaPort.obtenerPorUsuario(usuarioId).stream()
                .filter(c -> c.getId().equals(categoriaId))
                .map(this::toDto)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada o sin permiso"));
    }

    private CategoriaDto toDto(Categoria c) {
        return new CategoriaDto(c.getId(), c.getNombre(), c.getDescripcion(), 0);
    }
}