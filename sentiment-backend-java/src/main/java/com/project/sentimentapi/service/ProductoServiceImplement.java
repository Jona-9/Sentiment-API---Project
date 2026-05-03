package com.project.sentimentapi.service;


import com.project.sentimentapi.dto.ProductoDto;
import com.project.sentimentapi.dto.ProductoRequestDto;
import com.project.sentimentapi.entity.Categoria;
import com.project.sentimentapi.entity.Producto;
import com.project.sentimentapi.entity.User;
import com.project.sentimentapi.repository.CategoriaRepository;
import com.project.sentimentapi.repository.ProductoRepository;
import com.project.sentimentapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

    @Service
    public class ProductoServiceImplement implements ProductoService {

        @Autowired
        private ProductoRepository productoRepository;

        @Autowired
        private CategoriaRepository categoriaRepository;

        @Autowired
        private UserRepository userRepository;

        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        @Transactional
        public ProductoDto crearProducto(ProductoRequestDto request, Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            // ✅ Validar que la categoría pertenezca al usuario
            if (!categoria.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para usar esta categoría");
            }

            // ✅ Validar nombre duplicado
            productoRepository.findByNombreProductoAndUsuario(request.getNombreProducto(), usuario)
                    .ifPresent(p -> {
                        throw new RuntimeException("Ya existe un producto con este nombre");
                    });

            Producto producto = new Producto(request.getNombreProducto(), categoria, usuario);
            productoRepository.save(producto);

            return mapToDto(producto);
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductoDto> obtenerProductosPorUsuario(Integer usuarioId) {
            User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            List<Producto> productos = productoRepository.findByUsuarioOrderByUltimaActualizacionDesc(usuario);

            return productos.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<ProductoDto> obtenerProductosPorCategoria(Integer categoriaId, Integer usuarioId) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

            // ✅ Validar permisos
            if (!categoria.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para acceder a esta categoría");
            }

            List<Producto> productos = productoRepository.findByCategoriaOrderByNombreProductoAsc(categoria);

            return productos.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ProductoDto obtenerProductoPorId(Integer productoId, Integer usuarioId) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // ✅ Validar permisos
            if (!producto.getUsuario().getUsuarioID().equals(usuarioId)) {
                throw new RuntimeException("No tienes permiso para acceder a este producto");
            }

            return mapToDto(producto);
        }

        @Override
        @Transactional
        public void actualizarContadoresProducto(Integer productoId, int positivos, int negativos, int neutrales) {
            Producto producto = productoRepository.findById(productoId)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            // ✅ MÉTODO SEGURO: Usa el helper de la entidad
            producto.incrementarContadores(positivos, negativos, neutrales);
            productoRepository.save(producto);
        }

        // ✅ MAPPER
        private ProductoDto mapToDto(Producto producto) {
            ProductoDto dto = new ProductoDto();
            dto.setProductoId(producto.getProductoId());
            dto.setNombreProducto(producto.getNombreProducto());
            dto.setCategoriaId(producto.getCategoria().getCategoriaId());
            dto.setNombreCategoria(producto.getCategoria().getNombreCategoria());
            dto.setTotalMenciones(producto.getTotalMenciones());
            dto.setPositivos(producto.getPositivos());
            dto.setNegativos(producto.getNegativos());
            dto.setNeutrales(producto.getNeutrales());

            // ✅ Calcular porcentajes
            if (producto.getTotalMenciones() > 0) {
                dto.setPorcentajePositivos((producto.getPositivos() * 100.0) / producto.getTotalMenciones());
                dto.setPorcentajeNegativos((producto.getNegativos() * 100.0) / producto.getTotalMenciones());
                dto.setPorcentajeNeutrales((producto.getNeutrales() * 100.0) / producto.getTotalMenciones());
            } else {
                dto.setPorcentajePositivos(0.0);
                dto.setPorcentajeNegativos(0.0);
                dto.setPorcentajeNeutrales(0.0);
            }

            dto.setFechaCreacion(producto.getFechaCreacion().format(formatter));
            dto.setUltimaActualizacion(producto.getUltimaActualizacion().format(formatter));

            return dto;
        }
    }
