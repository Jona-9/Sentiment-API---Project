package com.project.sentimentapi.domain.model;

// MODELO DE DOMINIO (capa domain). POJO puro que representa el desglose de
// sentimiento de UN producto DENTRO de UNA sesión concreta (tabla puente
// sesión↔producto). Permite responder "¿cómo le fue a este producto en este
// análisis?" sin mezclarlo con el acumulado histórico que guarda Producto.
public class SesionProducto {

    private Integer id;
    private Integer sesionId;
    private Integer productoId;
    private Integer mencionesSesion;
    private Integer positivosSesion;
    private Integer negativosSesion;
    private Integer neutralesSesion;

    public SesionProducto() {}

    public SesionProducto(Integer id, Integer sesionId, Integer productoId,
                          Integer mencionesSesion, Integer positivosSesion,
                          Integer negativosSesion, Integer neutralesSesion) {
        this.id = id;
        this.sesionId = sesionId;
        this.productoId = productoId;
        this.mencionesSesion = mencionesSesion;
        this.positivosSesion = positivosSesion;
        this.negativosSesion = negativosSesion;
        this.neutralesSesion = neutralesSesion;
    }

    public SesionProducto(Integer sesionId, Integer productoId, Integer mencionesSesion,
                          Integer positivosSesion, Integer negativosSesion, Integer neutralesSesion) {
        this.sesionId = sesionId;
        this.productoId = productoId;
        this.mencionesSesion = mencionesSesion;
        this.positivosSesion = positivosSesion;
        this.negativosSesion = negativosSesion;
        this.neutralesSesion = neutralesSesion;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getSesionId() { return sesionId; }
    public void setSesionId(Integer sesionId) { this.sesionId = sesionId; }
    public Integer getProductoId() { return productoId; }
    public void setProductoId(Integer productoId) { this.productoId = productoId; }
    public Integer getMencionesSesion() { return mencionesSesion; }
    public void setMencionesSesion(Integer mencionesSesion) { this.mencionesSesion = mencionesSesion; }
    public Integer getPositivosSesion() { return positivosSesion; }
    public void setPositivosSesion(Integer positivosSesion) { this.positivosSesion = positivosSesion; }
    public Integer getNegativosSesion() { return negativosSesion; }
    public void setNegativosSesion(Integer negativosSesion) { this.negativosSesion = negativosSesion; }
    public Integer getNeutralesSesion() { return neutralesSesion; }
    public void setNeutralesSesion(Integer neutralesSesion) { this.neutralesSesion = neutralesSesion; }
}