package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.domain.model.Producto;
import com.project.sentimentapi.application.port.in.GestionarProductoUseCase;
import com.project.sentimentapi.domain.port.out.ProductoRepositoryPort;
import com.project.sentimentapi.application.dto.response.ProductoDto;
import com.project.sentimentapi.application.dto.request.ProductoRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Principio SRP: solo gestiona productos.
// Principio DIP: depende de ProductoRepositoryPort (interfaz), no de ProductoRepository (JPA).
@Service
public class GestionarProductoUseCaseImpl implements GestionarProductoUseCase {

    private final ProductoRepositoryPort productoPort;

    public GestionarProductoUseCaseImpl(ProductoRepositoryPort productoPort) {
        this.productoPort = productoPort;
    }

    @Override
    @Transactional
    public ProductoDto crearProducto(ProductoRequestDto request, Integer usuarioId) {
        // Validar duplicado por nombre
        productoPort.buscarPorNombre(request.getNombreProducto())
                .ifPresent(p -> {
                    throw new RuntimeException("Ya existe un producto con este nombre");
                });

        Producto nuevo = new Producto(null, request.getNombreProducto(),
                request.getCategoriaId(), 0, 0, 0, 0);
        Producto guardado = productoPort.guardar(nuevo);
        return toDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> obtenerProductosPorUsuario(Integer usuarioId) {
        return productoPort.obtenerActivos().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> obtenerProductosPorCategoria(Integer categoriaId, Integer usuarioId) {
        return productoPort.obtenerActivos().stream()
                .filter(p -> categoriaId.equals(p.getCategoriaId()))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto obtenerProductoPorId(Integer productoId, Integer usuarioId) {
        return productoPort.buscarPorId(productoId)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
    }

    @Override
    @Transactional
    public void actualizarContadoresProducto(Integer productoId, int positivos, int negativos, int neutrales) {
        Producto producto = productoPort.buscarPorId(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        producto.setTotalMenciones((producto.getTotalMenciones() != null ? producto.getTotalMenciones() : 0)
                + positivos + negativos + neutrales);
        producto.setPositivos((producto.getPositivos() != null ? producto.getPositivos() : 0) + positivos);
        producto.setNegativos((producto.getNegativos() != null ? producto.getNegativos() : 0) + negativos);
        producto.setNeutrales((producto.getNeutrales() != null ? producto.getNeutrales() : 0) + neutrales);
        productoPort.guardar(producto);
    }

    private ProductoDto toDto(Producto p) {
        ProductoDto dto = new ProductoDto();
        dto.setProductoId(p.getId());
        dto.setNombreProducto(p.getNombre());
        dto.setCategoriaId(p.getCategoriaId());
        dto.setTotalMenciones(p.getTotalMenciones() != null ? p.getTotalMenciones() : 0);
        dto.setPositivos(p.getPositivos() != null ? p.getPositivos() : 0);
        dto.setNegativos(p.getNegativos() != null ? p.getNegativos() : 0);
        dto.setNeutrales(p.getNeutrales() != null ? p.getNeutrales() : 0);
        int total = dto.getTotalMenciones();
        dto.setPorcentajePositivos(total > 0 ? (dto.getPositivos() * 100.0) / total : 0.0);
        dto.setPorcentajeNegativos(total > 0 ? (dto.getNegativos() * 100.0) / total : 0.0);
        dto.setPorcentajeNeutrales(total > 0 ? (dto.getNeutrales() * 100.0) / total : 0.0);
        return dto;
    }
}