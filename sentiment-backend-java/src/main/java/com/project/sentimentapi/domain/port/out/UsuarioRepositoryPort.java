package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Usuario;

import java.util.Optional;

// PORT OUT (puerto de salida — capa domain). Contrato de persistencia de Usuario en
// términos del dominio. Lo implementa UsuarioRepositoryAdapter. Barrera DIP.
public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Integer id);
    boolean existePorEmail(String email);
    Usuario guardar(Usuario usuario);
}