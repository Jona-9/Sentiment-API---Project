
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
    @Table(name = "producto")
    public class Producto {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "producto_id")
        private Integer productoId;

        @Column(name = "nombre_producto", nullable = false, length = 200)
        private String nombreProducto;

        @Column(name = "total_menciones", nullable = false)
        private Integer totalMenciones = 0;

        @Column(name = "positivos", nullable = false)
        private Integer positivos = 0;

        @Column(name = "negativos", nullable = false)
        private Integer negativos = 0;

        @Column(name = "neutrales", nullable = false)
        private Integer neutrales = 0;

        // ✅ Metadatos
        @Column(name = "fecha_creacion", nullable = false, updatable = false)
        private LocalDateTime fechaCreacion;

        @Column(name = "ultima_actualizacion")
        private LocalDateTime ultimaActualizacion;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "categoria_id", nullable = false)
        private Categoria categoria;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "usuario_id", nullable = false)
        private User usuario;

        @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
        private List<Sesion> sesiones = new ArrayList<>();

        @PrePersist
        protected void onCreate() {
            fechaCreacion = LocalDateTime.now();
            ultimaActualizacion = LocalDateTime.now();
        }

        @PreUpdate
        protected void onUpdate() {
            ultimaActualizacion = LocalDateTime.now();
        }

        public Producto(String nombreProducto, Categoria categoria, User usuario) {
            this.nombreProducto = nombreProducto;
            this.categoria = categoria;
            this.usuario = usuario;
        }

        public void incrementarContadores(int positivos, int negativos, int neutrales) {
            this.positivos += positivos;
            this.negativos += negativos;
            this.neutrales += neutrales;
            this.totalMenciones = this.positivos + this.negativos + this.neutrales;
        }

        public double getPorcentajePositivos() {
            if (totalMenciones == 0) return 0.0;
            return (positivos * 100.0) / totalMenciones;
        }
    }
