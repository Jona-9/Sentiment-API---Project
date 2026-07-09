package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Comentario;

import java.util.List;

public interface ComentarioRepositoryPort {
    Comentario guardar(Comentario comentario);
    List<Comentario> guardarTodos(List<Comentario> comentarios);
    List<Comentario> buscarPorSesion(Integer sesionId);
}