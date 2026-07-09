package com.project.sentimentapi.application.usecase;

import com.project.sentimentapi.application.mapper.SesionMapper;
import com.project.sentimentapi.domain.model.Comentario;
import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.domain.port.in.ConsultarSesionesUseCase;
import com.project.sentimentapi.domain.port.out.ComentarioRepositoryPort;
import com.project.sentimentapi.domain.port.out.SesionRepositoryPort;
import com.project.sentimentapi.presentation.dto.response.ComentarioDto;
import com.project.sentimentapi.presentation.dto.response.SesionDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConsultarSesionesUseCaseImpl implements ConsultarSesionesUseCase {

    private final SesionRepositoryPort sesionPort;
    private final ComentarioRepositoryPort comentarioPort;

    public ConsultarSesionesUseCaseImpl(SesionRepositoryPort sesionPort,
                                        ComentarioRepositoryPort comentarioPort) {
        this.sesionPort = sesionPort;
        this.comentarioPort = comentarioPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SesionDto> obtenerPorUsuario(Integer usuarioId) {
        // Para el listado del historial solo necesitamos estadísticas, no comentarios
        return sesionPort.buscarPorUsuario(usuarioId).stream()
                .map(SesionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SesionDto obtenerPorId(Integer sesionId) {
        Sesion sesion = sesionPort.buscarPorId(sesionId)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada con id: " + sesionId));

        SesionDto dto = SesionMapper.toDto(sesion);

        // CORRECCIÓN: incluir los comentarios guardados en BD para que
        // "Ver análisis" del historial pueda mostrar el detalle de cada comentario
        List<Comentario> comentarios = comentarioPort.buscarPorSesion(sesionId);
        List<ComentarioDto> comentariosDto = comentarios.stream()
                .map(c -> new ComentarioDto(c.getTexto(), c.getSentimiento(), c.getProbabilidad()))
                .collect(Collectors.toList());

        dto.setComentarios(comentariosDto);
        return dto;
    }
}