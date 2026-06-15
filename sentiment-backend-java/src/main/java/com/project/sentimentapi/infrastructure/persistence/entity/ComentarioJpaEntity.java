package com.project.sentimentapi.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "comentario")
public class ComentarioJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comentario_id")
    private Integer comentarioId;

    @Column(name = "texto", nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(name = "sentimiento", nullable = false)
    private String sentimiento;

    @Column(name = "probabilidad", nullable = false)
    private Double probabilidad;

    @ManyToOne
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionJpaEntity sesion;

    public ComentarioJpaEntity(String texto, String sentimiento,
                                Double probabilidad, SesionJpaEntity sesion) {
        this.texto = texto;
        this.sentimiento = sentimiento;
        this.probabilidad = probabilidad;
        this.sesion = sesion;
    }
}
