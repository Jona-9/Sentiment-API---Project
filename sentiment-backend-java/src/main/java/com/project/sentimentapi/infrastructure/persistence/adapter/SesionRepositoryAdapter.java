package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.domain.port.out.SesionRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.SesionJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.SesionJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Patrón ADAPTER: convierte entre SesionRepositoryPort (dominio) y SesionJpaRepository (JPA)
@Component
public class SesionRepositoryAdapter implements SesionRepositoryPort {

    private final SesionJpaRepository sesionJpaRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public SesionRepositoryAdapter(SesionJpaRepository sesionJpaRepository,
                                    UsuarioJpaRepository usuarioJpaRepository) {
        this.sesionJpaRepository = sesionJpaRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Sesion guardar(Sesion sesion) {
        UsuarioJpaEntity usuario = usuarioJpaRepository.findById(sesion.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Usuario no encontrado con id: " + sesion.getUsuarioId()));

        SesionJpaEntity entity = new SesionJpaEntity(
                sesion.getFecha(),
                sesion.getAvgScore(),
                sesion.getTotalComentarios(),
                sesion.getPositivos(),
                sesion.getNegativos(),
                sesion.getNeutrales(),
                usuario
        );
        return entityToDomain(sesionJpaRepository.save(entity));
    }

    @Override
    public Optional<Sesion> buscarPorId(Integer id) {
        return sesionJpaRepository.findById(id).map(this::entityToDomain);
    }

    @Override
    public List<Sesion> buscarPorUsuario(Integer usuarioId) {
        return sesionJpaRepository.findByUsuarioUsuarioIDOrderByFechaDescSesionIdDesc(usuarioId).stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    private Sesion entityToDomain(SesionJpaEntity entity) {
        return new Sesion(
                entity.getSesionId(),
                entity.getUsuario() != null ? entity.getUsuario().getUsuarioID() : null,
                entity.getFecha(),
                entity.getTotal(),
                entity.getPositivos(),
                entity.getNegativos(),
                entity.getNeutrales(),
                entity.getAvgScore()
        );
    }
}
