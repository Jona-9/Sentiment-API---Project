package com.project.sentimentapi.domain.port.out;

import com.project.sentimentapi.domain.model.Comentario;

import java.util.List;

// PORT OUT (puerto de salida — capa domain). Contrato de persistencia de Comentario
// (guardado individual/por lote y consulta por sesión). Lo implementa
// ComentarioRepositoryAdapter. Barrera DIP.
public interface ComentarioRepositoryPort {
    Comentario guardar(Comentario comentario);
    List<Comentario> guardarTodos(List<Comentario> comentarios);
    List<Comentario> buscarPorSesion(Integer sesionId);
}