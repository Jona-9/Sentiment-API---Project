package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.SesionProducto;
import com.project.sentimentapi.domain.port.out.SesionProductoRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.ProductoJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.SesionJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.SesionProductoJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.ProductoJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.SesionJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.SesionProductoJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// Patrón ADAPTER: convierte entre SesionProductoRepositoryPort (dominio) y SesionProductoJpaRepository (JPA)
@Component
public class SesionProductoRepositoryAdapter implements SesionProductoRepositoryPort {

    private final SesionProductoJpaRepository sesionProductoJpaRepository;
    private final SesionJpaRepository sesionJpaRepository;
    private final ProductoJpaRepository productoJpaRepository;

    public SesionProductoRepositoryAdapter(SesionProductoJpaRepository sesionProductoJpaRepository,
                                            SesionJpaRepository sesionJpaRepository,
                                            ProductoJpaRepository productoJpaRepository) {
        this.sesionProductoJpaRepository = sesionProductoJpaRepository;
        this.sesionJpaRepository = sesionJpaRepository;
        this.productoJpaRepository = productoJpaRepository;
    }

    @Override
    public SesionProducto guardar(SesionProducto sesionProducto) {
        SesionJpaEntity sesion = sesionJpaRepository.findById(sesionProducto.getSesionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sesión no encontrada con id: " + sesionProducto.getSesionId()));
        ProductoJpaEntity producto = productoJpaRepository.findById(sesionProducto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producto no encontrado con id: " + sesionProducto.getProductoId()));

        SesionProductoJpaEntity entity = new SesionProductoJpaEntity(
                sesion, producto,
                sesionProducto.getMencionesSesion(),
                sesionProducto.getPositivosSesion(),
                sesionProducto.getNegativosSesion(),
                sesionProducto.getNeutralesSesion()
        );
        return entityToDomain(sesionProductoJpaRepository.save(entity));
    }

    @Override
    public List<SesionProducto> guardarTodos(List<SesionProducto> items) {
        List<SesionProductoJpaEntity> entities = items.stream()
                .map(sp -> {
                    SesionJpaEntity sesion = sesionJpaRepository.findById(sp.getSesionId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Sesión no encontrada: " + sp.getSesionId()));
                    ProductoJpaEntity producto = productoJpaRepository.findById(sp.getProductoId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Producto no encontrado: " + sp.getProductoId()));
                    return new SesionProductoJpaEntity(
                            sesion, producto,
                            sp.getMencionesSesion(), sp.getPositivosSesion(),
                            sp.getNegativosSesion(), sp.getNeutralesSesion());
                })
                .collect(Collectors.toList());
        return sesionProductoJpaRepository.saveAll(entities).stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SesionProducto> buscarPorSesion(Integer sesionId) {
        return sesionJpaRepository.findById(sesionId)
                .map(sesion -> sesionProductoJpaRepository.findBySesion(sesion).stream()
                        .map(this::entityToDomain)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    @Override
    public List<SesionProducto> buscarProductosUltimaSesion(Integer usuarioId) {
        return sesionProductoJpaRepository.findProductosUltimaSesion(usuarioId).stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    private SesionProducto entityToDomain(SesionProductoJpaEntity entity) {
        return new SesionProducto(
                entity.getSesionProductoId(),
                entity.getSesion() != null ? entity.getSesion().getSesionId() : null,
                entity.getProducto() != null ? entity.getProducto().getProductoId() : null,
                entity.getMencionesSesion(),
                entity.getPositivosSesion(),
                entity.getNegativosSesion(),
                entity.getNeutralesSesion()
        );
    }
}
