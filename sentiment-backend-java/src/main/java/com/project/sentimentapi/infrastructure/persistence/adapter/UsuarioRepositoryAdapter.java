package com.project.sentimentapi.infrastructure.persistence.adapter;

import com.project.sentimentapi.domain.model.Usuario;
import com.project.sentimentapi.domain.port.out.UsuarioRepositoryPort;
import com.project.sentimentapi.infrastructure.persistence.entity.UsuarioJpaEntity;
import com.project.sentimentapi.infrastructure.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

// Patrón ADAPTER: convierte entre UsuarioRepositoryPort (dominio) y UsuarioJpaRepository (JPA)
@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Usuario> buscarPorEmail(String email) {
        // correo en la entidad JPA ↔ email en el modelo de dominio
        return jpaRepository.findByCorreo(email).map(this::entityToDomain);
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return jpaRepository.findById(id).map(this::entityToDomain);
    }

    @Override
    public boolean existePorEmail(String email) {
        return jpaRepository.existsByCorreo(email);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        UsuarioJpaEntity entity = domainToEntity(usuario);
        return entityToDomain(jpaRepository.save(entity));
    }

    // Infraestructura (JPA) → Dominio (POJO puro)
    private Usuario entityToDomain(UsuarioJpaEntity entity) {
        return new Usuario(
                entity.getUsuarioID(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getCorreo(),       // correo → email
                entity.getContrasena()    // contrasena → passwordHash
        );
    }

    // Dominio (POJO puro) → Infraestructura (JPA)
    private UsuarioJpaEntity domainToEntity(Usuario usuario) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        if (usuario.getId() != null) entity.setUsuarioID(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setApellido(usuario.getApellido());
        entity.setCorreo(usuario.getEmail());           // email → correo
        entity.setContrasena(usuario.getPasswordHash()); // passwordHash → contrasena
        return entity;
    }
}
