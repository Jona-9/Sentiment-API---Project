package com.project.sentimentapi.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

    @Entity
    @Data
    @NoArgsConstructor
    @Table(name = "categoria")
    public class Categoria {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "categoria_id")
        private Integer categoriaId;

        @Column(name = "nombre_categoria", nullable = false, length = 100)
        private String nombreCategoria;

        @Column(name = "descripcion", length = 255)
        private String descripcion;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "usuario_id", nullable = false)
        private User usuario;

        @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Producto> productos = new ArrayList<>();

        public Categoria(String nombreCategoria, String descripcion, User usuario) {
            this.nombreCategoria = nombreCategoria;
            this.descripcion = descripcion;
            this.usuario = usuario;
        }
    }