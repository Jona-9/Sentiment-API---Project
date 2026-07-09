package com.project.sentimentapi.application.mapper;

import com.project.sentimentapi.domain.model.Producto;
import com.project.sentimentapi.presentation.dto.response.*;

public class ProductoMapper {

    private ProductoMapper() {}

    public static ProductoDto toDto(Producto producto) {
        if (producto == null) return null;

        ProductoDto dto = new ProductoDto();
        dto.setProductoId(producto.getId());
        dto.setNombreProducto(producto.getNombre());
        dto.setCategoriaId(producto.getCategoriaId());
        dto.setTotalMenciones(producto.getTotalMenciones());
        dto.setPositivos(producto.getPositivos());
        dto.setNegativos(producto.getNegativos());
        dto.setNeutrales(producto.getNeutrales());

        int total = producto.getTotalMenciones() != null ? producto.getTotalMenciones() : 0;
        if (total > 0) {
            dto.setPorcentajePositivos((producto.getPositivos() * 100.0) / total);
            dto.setPorcentajeNegativos((producto.getNegativos() * 100.0) / total);
            dto.setPorcentajeNeutrales((producto.getNeutrales() * 100.0) / total);
        } else {
            dto.setPorcentajePositivos(0.0);
            dto.setPorcentajeNegativos(0.0);
            dto.setPorcentajeNeutrales(0.0);
        }

        return dto;
    }
}