package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> buscarPorEmail(String email);
    Optional<Usuario> buscarPorId(Integer id);
    boolean existePorEmail(String email);
    Usuario guardar(Usuario usuario);
}