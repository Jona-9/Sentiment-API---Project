package com.project.sentimentapi.domain.model;

public class Rol {

    private Integer id;
    private String nombreRol;

    public Rol() {}

    public Rol(Integer id, String nombreRol) {
        this.id = id;
        this.nombreRol = nombreRol;
    }

    public Rol(String nombreRol) {
        this.nombreRol = nombreRol;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}