package com.project.sentimentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "sesion")
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sesion_id")
    private Integer sesionId;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "avg_score", nullable = false)
    private Double avgScore;

    @Column(name = "total", nullable = false)
    private Integer total;

    @Column(name = "positivos", nullable = false)
    private Integer positivos;

    @Column(name = "negativos", nullable = false)
    private Integer negativos;

    @Column(name = "neutrales", nullable = false)
    private Integer neutrales;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User usuario;

    // ✅ NUEVA RELACIÓN: Una sesión tiene muchos comentarios
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "sesion", fetch = FetchType.LAZY)
    private List<Comentario> comentarios = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id") // ← NULLABLE (puede ser análisis sin producto)
    private Producto producto;
    public Sesion(LocalDateTime fecha, Double avgScore, Integer total,
                  Integer positivos, Integer negativos, Integer neutrales, User usuario) {
        this.fecha = fecha;
        this.avgScore = avgScore;
        this.total = total;
        this.positivos = positivos;
        this.negativos = negativos;
        this.neutrales = neutrales;
        this.usuario = usuario;
    }
}