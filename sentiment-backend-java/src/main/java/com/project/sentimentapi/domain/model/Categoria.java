package com.project.sentimentapi.domain.model;

public class Categoria {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer usuarioId;

    public Categoria() {}

    public Categoria(Integer id, String nombre, String descripcion, Integer usuarioId) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

    public Categoria(String nombre, String descripcion, Integer usuarioId) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.usuarioId = usuarioId;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}