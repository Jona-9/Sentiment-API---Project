package com.project.sentimentapi.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SesionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sesion_producto_id")
    private Integer sesionProductoId;

    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    // Estadísticas específicas de este producto EN ESTA sesión
    @Column(name = "menciones_sesion", nullable = false)
    private Integer mencionesSesion = 0;

    @Column(name = "positivos_sesion", nullable = false)
    private Integer positivosSesion = 0;

    @Column(name = "negativos_sesion", nullable = false)
    private Integer negativosSesion = 0;

    @Column(name = "neutrales_sesion", nullable = false)
    private Integer neutralesSesion = 0;

    public SesionProducto(Sesion sesion, Producto producto, Integer menciones,
                          Integer positivos, Integer negativos, Integer neutrales) {
        this.sesion = sesion;
        this.producto = producto;
        this.mencionesSesion = menciones;
        this.positivosSesion = positivos;
        this.negativosSesion = negativos;
        this.neutralesSesion = neutrales;
    }
}
