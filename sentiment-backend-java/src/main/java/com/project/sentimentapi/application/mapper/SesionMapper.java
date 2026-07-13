package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Sesion;
import com.project.sentimentapi.application.dto.response.*;
// MAPPER (capa application). Traduce el modelo de dominio Sesion al DTO de salida
// SesionDto que consume el dashboard (historial). Convierte la fecha a String.
// Utilidad estática sin estado (constructor privado).
public class SesionMapper {

    private SesionMapper() {} // constructor privado: clase de utilidad, no instanciable

    public static SesionDto toDto(Sesion sesion) {
        if (sesion == null) return null;
        SesionDto dto = new SesionDto();
        dto.setSesionId(sesion.getId());
        dto.setFecha(sesion.getFecha() != null ? sesion.getFecha().toString() : null);
        dto.setAvgScore(sesion.getAvgScore());
        dto.setTotal(sesion.getTotalComentarios());
        dto.setPositivos(sesion.getPositivos());
        dto.setNegativos(sesion.getNegativos());
        dto.setNeutrales(sesion.getNeutrales());
        return dto;
    }
}