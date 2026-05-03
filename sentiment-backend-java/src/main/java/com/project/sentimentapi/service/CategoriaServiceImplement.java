package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.CategoriaDto;
import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.CategoriaRepository;
import com.project.sentimentapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

    @Service
    public class CategoriaServiceImplement implements CategoriaService {

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private UserRepository userRepository;

        @Override
        @Transactional
        public CategoriaDto crearCategoria(String nombreCategoria, String descripcion, Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // ✅ Validar que no exista categoría con el mismo nombre para este usuario
            categoriaRepository.findByNombreCategoriaAndUsuario(nombreCategoria, usuario)
                    .ifPresent(c -> {
                        throw new RuntimeException("Ya existe una categoría con este nombre");
                    });

            Categoria categoria = new Categoria(nombreCategoria, descripcion, usuario);
            categoriaRepository.save(categoria);

            return mapToDto(categoria);
        }

        @Override
        @Transactional(readOnly = true)
        public List<CategoriaDto> obtenerCategoriasPorUsuario(Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Categoria> categorias = categoriaRepository.findByUsuarioOrderByNombreCategoriaAsc(usuario);

            return categorias.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public CategoriaDto obtenerCategoriaPorId(Integer categoriaId, Integer usuarioId) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            // ✅ Validar que la categoría pertenezca al usuario
            if (!categoria.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para acceder a esta categoría");
            }

            return mapToDto(categoria);
        }

        // ✅ MAPPER
        private CategoriaDto mapToDto(Categoria categoria) {
            return new CategoriaDto(
                    categoria.getCategoriaId(),
                    categoria.getNombreCategoria(),
                    categoria.getDescripcion(),
                    categoria.getProductos().size()
            );
        }
    }
