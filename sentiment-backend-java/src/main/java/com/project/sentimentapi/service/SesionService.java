package com.project.sentimentapi.service;

import com.project.sentimentapi.dto.CsvEntradaDto;
import com.project.sentimentapi.dto.SesionDto;
import com.project.sentimentapi.dto.SesionPreviaInfoDto;
import jakarta.transaction.Transactional;

import java.util.List;

public interface SesionService {
    void guardarSesion(SesionDto sesionDto, Integer usuarioId);
    List<SesionDto> obtenerSesionesPorUsuario(Integer usuarioId);
    SesionDto analizarYGuardarComentarios(List<String> comentarios, Integer usuarioId);

    @Transactional
    SesionDto analizarYGuardarConProducto(
            List<String> comentarios,
            Integer usuarioId,
            Integer productoId
    );

    // ✨ NUEVOS MÉTODOS
    SesionPreviaInfoDto obtenerProductosUltimaSesion(Integer usuarioId);

    @Transactional
    SesionDto analizarConMismosProductos(
            List<String> comentarios,
            Integer usuarioId,
            Integer sesionPreviaId
    );

    @Transactional
    SesionDto analizarConMultiplesProductos(
            List<String> comentarios,
            Integer usuarioId,
            List<Integer> productosIds
    );

    @Transactional
    SesionDto analizarBatchConCsv(
            List<CsvEntradaDto> entradas,
            Integer usuarioId
    );
}