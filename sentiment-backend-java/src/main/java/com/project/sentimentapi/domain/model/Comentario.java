package com.project.sentimentapi.domain.model;

public class Comentario {

    private Integer id;
    private String texto;
    private String sentimiento;
    private Double probabilidad;
    private Integer sesionId;
    private String producto; // nombre del producto asociado al comentario (puede ser null)

    public Comentario() {}

    public Comentario(Integer id, String texto, String sentimiento,
                      Double probabilidad, Integer sesionId) {
        this(id, texto, sentimiento, probabilidad, sesionId, null);
    }

    public Comentario(Integer id, String texto, String sentimiento,
                      Double probabilidad, Integer sesionId, String producto) {
        this.id = id;
        this.texto = texto;
        this.sentimiento = sentimiento;
        this.probabilidad = probabilidad;
        this.sesionId = sesionId;
        this.producto = producto;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getSentimiento() { return sentimiento; }
    public void setSentimiento(String sentimiento) { this.sentimiento = sentimiento; }
    public Double getProbabilidad() { return probabilidad; }
    public void setProbabilidad(Double probabilidad) { this.probabilidad = probabilidad; }
    public Integer getSesionId() { return sesionId; }
    public void setSesionId(Integer sesionId) { this.sesionId = sesionId; }
    public String getProducto() { return producto; }
    public void setProducto(String producto) { this.producto = producto; }
}