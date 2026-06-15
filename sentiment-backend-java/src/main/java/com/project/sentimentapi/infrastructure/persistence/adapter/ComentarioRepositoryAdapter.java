package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Comentario;
import com.project.sentimentapi.domain.port.out.ComentarioRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.ComentarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.entity.SesionJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.ComentarioJpaRepository;
import com.project.sentimentapi.infrastructure.persistence.repository.SesionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

// Patrón ADAPTER: convierte entre ComentarioRepositoryPort (dominio) y ComentarioJpaRepository (JPA)
@Component
public class ComentarioRepositoryAdapter implements ComentarioRepositoryPort {

    private final ComentarioJpaRepository comentarioJpaRepository;
    private final SesionJpaRepository sesionJpaRepository;

    public ComentarioRepositoryAdapter(ComentarioJpaRepository comentarioJpaRepository,
                                        SesionJpaRepository sesionJpaRepository) {
        this.comentarioJpaRepository = comentarioJpaRepository;
        this.sesionJpaRepository = sesionJpaRepository;
    }

    @Override
    public Comentario guardar(Comentario comentario) {
        SesionJpaEntity sesion = sesionJpaRepository.findById(comentario.getSesionId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sesión no encontrada con id: " + comentario.getSesionId()));
        ComentarioJpaEntity entity = new ComentarioJpaEntity(
                comentario.getTexto(),
                comentario.getSentimiento(),
                comentario.getProbabilidad(),
                sesion
        );
        return entityToDomain(comentarioJpaRepository.save(entity));
    }

    @Override
    public List<Comentario> guardarTodos(List<Comentario> comentarios) {
        List<ComentarioJpaEntity> entities = comentarios.stream()
                .map(c -> {
                    SesionJpaEntity sesion = sesionJpaRepository.findById(c.getSesionId())
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "Sesión no encontrada con id: " + c.getSesionId()));
                    return new ComentarioJpaEntity(
                            c.getTexto(), c.getSentimiento(), c.getProbabilidad(), sesion);
                })
                .collect(Collectors.toList());
        return comentarioJpaRepository.saveAll(entities).stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comentario> buscarPorSesion(Integer sesionId) {
        return comentarioJpaRepository.findBySesionSesionId(sesionId).stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    private Comentario entityToDomain(ComentarioJpaEntity entity) {
        return new Comentario(
                entity.getComentarioId(),
                entity.getTexto(),
                entity.getSentimiento(),
                entity.getProbabilidad(),
                entity.getSesion() != null ? entity.getSesion().getSesionId() : null
        );
    }
}
