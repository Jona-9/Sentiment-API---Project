package com.project.sentimentapi.application.builder;

import com.project.sentimentapi.domain.model.Sesion;

import java.time.LocalDateTime;

// Patrón BUILDER (GoF): NO es un bean singleton de Spring. Se instancia con `new` por cada
// construcción para que el estado mutable no se comparta entre hilos HTTP concurrentes
// (dos usuarios analizando a la vez mezclaban datos cuando esto era un @Component).
public class SesionBuilder {

    private Integer usuarioId;
    private LocalDateTime fecha;
    private int totalComentarios;
    private int positivos;
    private int negativos;
    private int neutrales;
    private double avgScore;

    public SesionBuilder reset() {
        this.usuarioId = null;
        this.fecha = null;
        this.totalComentarios = 0;
        this.positivos = 0;
        this.negativos = 0;
        this.neutrales = 0;
        this.avgScore = 0.0;
        return this;
    }

    public SesionBuilder conUsuario(Integer usuarioId) {
        this.usuarioId = usuarioId;
        return this;
    }

    public SesionBuilder conFecha(LocalDateTime fecha) {
        this.fecha = fecha;
        return this;
    }

    public SesionBuilder conTotalComentarios(int total) {
        this.totalComentarios = total;
        return this;
    }

    public SesionBuilder conEstadisticas(int positivos, int negativos,
                                         int neutrales, double avgScore) {
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.avgScore = avgScore;
        return this;
    }

    public Sesion build() {
        if (usuarioId == null) {
            throw new IllegalStateException("SesionBuilder: usuarioId es obligatorio");
        }
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }

        return new Sesion(usuarioId, fecha, totalComentarios,
                positivos, negativos, neutrales, avgScore);
    }
}