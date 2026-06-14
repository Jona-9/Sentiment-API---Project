package com.project.sentimentapi.domain.model;

public class Producto {

    private Integer id;
    private String nombre;
    private Integer categoriaId;
    private Integer totalMenciones;
    private Integer positivos;
    private Integer negativos;
    private Integer neutrales;

    public Producto() {}

    public Producto(Integer id, String nombre, Integer categoriaId,
                    Integer totalMenciones, Integer positivos,
                    Integer negativos, Integer neutrales) {
        this.id = id;
        this.nombre = nombre;
        this.categoriaId = categoriaId;
        this.totalMenciones = totalMenciones;
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public Integer getTotalMenciones() { return totalMenciones; }
    public void setTotalMenciones(Integer totalMenciones) { this.totalMenciones = totalMenciones; }
    public Integer getPositivos() { return positivos; }
    public void setPositivos(Integer positivos) { this.positivos = positivos; }
    public Integer getNegativos() { return negativos; }
    public void setNegativos(Integer negativos) { this.negativos = negativos; }
    public Integer getNeutrales() { return neutrales; }
    public void setNeutrales(Integer neutrales) { this.neutrales = neutrales; }
}