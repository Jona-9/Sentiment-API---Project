package com.project.sentimentapi.domain.model;

import java.time.LocalDateTime;

public class Sesion {

    private Integer id;
    private Integer usuarioId;
    private LocalDateTime fecha;
    private Integer total;
    private Integer positivos;
    private Integer negativos;
    private Integer neutrales;
    private Double avgScore;

    public Sesion() {}

    // Constructor sin id (para creación) — usado por SesionBuilder
    public Sesion(Integer usuarioId, LocalDateTime fecha, Integer total,
                  Integer positivos, Integer negativos, Integer neutrales,
                  Double avgScore) {
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.total = total;
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.avgScore = avgScore;
    }

    public Sesion(Integer id, Integer usuarioId, LocalDateTime fecha, Integer total,
                  Integer positivos, Integer negativos, Integer neutrales,
                  Double avgScore) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fecha = fecha;
        this.total = total;
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.avgScore = avgScore;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public Integer getTotalComentarios() { return total; }
    public void setTotalComentarios(Integer total) { this.total = total; }
    public Integer getPositivos() { return positivos; }
    public void setPositivos(Integer positivos) { this.positivos = positivos; }
    public Integer getNegativos() { return negativos; }
    public void setNegativos(Integer negativos) { this.negativos = negativos; }
    public Integer getNeutrales() { return neutrales; }
    public void setNeutrales(Integer neutrales) { this.neutrales = neutrales; }
    public Double getAvgScore() { return avgScore; }
    public void setAvgScore(Double avgScore) { this.avgScore = avgScore; }
}