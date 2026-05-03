package com.project.sentimentapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comentario_id")
    private Integer comentarioId;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(name = "sentimiento", nullable = false)
    private String sentimiento; // "Positivo", "Negativo", "Neutral"

    @Column(name = "probabilidad", nullable = false)
    private Double probabilidad;

    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private Sesion sesion;

    public Comentario(String texto, String sentimiento, Double probabilidad, Sesion sesion) {
        this.texto = texto;
        this.sentimiento = sentimiento;
        this.probabilidad = probabilidad;
        this.sesion = sesion;
    }
}